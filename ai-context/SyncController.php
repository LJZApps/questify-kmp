<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Quest;
use App\Models\QuestCategory;
use App\Models\SubQuest;
use Carbon\Carbon;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class SyncController extends Controller
{
    public function sync(Request $request)
    {
        $user = $request->user();

        $data = $request->validate([
            'categories' => 'array',
            'categories.*.uuid' => 'required|uuid',
            'categories.*.text' => 'required|string',
            'categories.*.updated_at' => 'nullable|date',
            'categories.*.deleted_at' => 'nullable|date',

            'quests' => 'array',
            'quests.*.uuid' => 'required|uuid',
            'quests.*.category_uuid' => 'nullable|uuid',
            'quests.*.title' => 'required|string',
            'quests.*.notes' => 'nullable|string',
            'quests.*.difficulty' => 'required|string',
            'quests.*.done' => 'boolean',
            'quests.*.updated_at' => 'nullable|date',
            'quests.*.deleted_at' => 'nullable|date',

            'sub_quests' => 'array',
            'sub_quests.*.uuid' => 'required|uuid',
            'sub_quests.*.quest_uuid' => 'required|uuid',
            'sub_quests.*.text' => 'required|string',
            'sub_quests.*.is_done' => 'boolean',
            'sub_quests.*.order_index' => 'integer',
            'sub_quests.*.updated_at' => 'nullable|date',
            'sub_quests.*.deleted_at' => 'nullable|date',

            'last_sync_timestamp' => 'nullable|date'
        ]);

        $lastSync = !empty($data['last_sync_timestamp'])
            ? Carbon::parse($data['last_sync_timestamp'])
            : Carbon::createFromTimestamp(0);

        $now = now();

        $syncedUuids = [
            'categories' => [],
            'quests' => [],
            'sub_quests' => [],
        ];

        DB::transaction(function () use ($data, $user, $now, &$syncedUuids) {
            // 1. Sync Categories
            foreach ($data['categories'] ?? [] as $catData) {
                $syncedUuids['categories'][] = $catData['uuid'];
                QuestCategory::withTrashed()->updateOrCreate(
                    ['uuid' => $catData['uuid'], 'user_id' => $user->id],
                    [
                        'text' => $catData['text'],
                        'updated_at' => $now,
                        'deleted_at' => !empty($catData['deleted_at']) ? Carbon::parse($catData['deleted_at']) : null,
                    ]
                );
            }

            // 2. Sync Quests
            foreach ($data['quests'] ?? [] as $questData) {
                $syncedUuids['quests'][] = $questData['uuid'];
                $categoryId = null;
                if (!empty($questData['category_uuid'])) {
                    $categoryId = QuestCategory::where('uuid', $questData['category_uuid'])
                        ->where('user_id', $user->id)
                        ->value('id');
                }

                Quest::withTrashed()->updateOrCreate(
                    ['uuid' => $questData['uuid'], 'user_id' => $user->id],
                    [
                        'category_id' => $categoryId,
                        'title' => $questData['title'],
                        'notes' => $questData['notes'] ?? null,
                        'difficulty' => $questData['difficulty'],
                        'done' => $questData['done'] ?? false,
                        'updated_at' => $now,
                        'deleted_at' => !empty($questData['deleted_at']) ? Carbon::parse($questData['deleted_at']) : null,
                    ]
                );
            }

            // 3. Sync SubQuests
            foreach ($data['sub_quests'] ?? [] as $subData) {
                $syncedUuids['sub_quests'][] = $subData['uuid'];
                $quest = Quest::where('uuid', $subData['quest_uuid'])
                    ->where('user_id', $user->id)
                    ->first();
                if (!$quest) continue;

                SubQuest::withTrashed()->updateOrCreate(
                    ['uuid' => $subData['uuid']],
                    [
                        'quest_id' => $quest->id,
                        'text' => $subData['text'],
                        'is_done' => $subData['is_done'] ?? false,
                        'order_index' => $subData['order_index'] ?? 0,
                        'updated_at' => $now,
                        'deleted_at' => !empty($subData['deleted_at']) ? Carbon::parse($subData['deleted_at']) : null,
                    ]
                );
            }
        });

        $serverCategories = QuestCategory::withTrashed()
            ->where('user_id', $user->id)
            ->where('updated_at', '>', $lastSync)
            ->where('updated_at', '<=', $now)
            ->whereNotIn('uuid', $syncedUuids['categories'])
            ->get();

        $serverQuests = Quest::withTrashed()
            ->where('user_id', $user->id)
            ->where('updated_at', '>', $lastSync)
            ->where('updated_at', '<=', $now)
            ->whereNotIn('uuid', $syncedUuids['quests'])
            ->get();

        $serverSubQuests = SubQuest::withTrashed()
            ->whereHas('quest', function($query) use ($user) {
                $query->where('user_id', $user->id);
            })
            ->where('updated_at', '>', $lastSync)
            ->where('updated_at', '<=', $now)
            ->whereNotIn('uuid', $syncedUuids['sub_quests'])
            ->get();

        return response()->json([
            'categories' => $serverCategories->map(fn($c) => [
                'uuid' => $c->uuid,
                'text' => $c->text,
                'updated_at' => $c->updated_at->toIso8601String(),
                'deleted_at' => $c->deleted_at?->toIso8601String(),
            ]),
            'quests' => $serverQuests->map(fn($q) => [
                'uuid' => $q->uuid,
                'category_uuid' => $q->category?->uuid,
                'title' => $q->title,
                'notes' => $q->notes,
                'difficulty' => $q->difficulty,
                'done' => (bool) $q->done,
                'updated_at' => $q->updated_at->toIso8601String(),
                'deleted_at' => $q->deleted_at?->toIso8601String(),
            ]),
            'sub_quests' => $serverSubQuests->map(fn($s) => [
                'uuid' => $s->uuid,
                'quest_uuid' => $s->quest->uuid,
                'text' => $s->text,
                'is_done' => (bool) $s->is_done,
                'order_index' => $s->order_index,
                'updated_at' => $s->updated_at->toIso8601String(),
                'deleted_at' => $s->deleted_at?->toIso8601String(),
            ]),
            'new_timestamp' => $now->toIso8601String()
        ]);
    }
}
