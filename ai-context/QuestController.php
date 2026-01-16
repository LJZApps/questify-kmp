<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Quest;
use Illuminate\Http\Request;
use OpenApi\Attributes as OA;

class QuestController extends Controller
{
    #[OA\Get(
        path: "/api/quests",
        summary: "List all quests / Alle Quests auflisten",
        security: [["sanctum" => []]],
        tags: ["Quests"],
        responses: [
            new OA\Response(
                response: 200,
                description: "List of quests / Liste der Quests",
                content: new OA\JsonContent(type: "array", items: new OA\Items(ref: "#/components/schemas/Quest"))
            )
        ]
    )]
    public function index(Request $request)
    {
        return $request->user()->quests()->with('subQuests')->get();
    }

    #[OA\Post(
        path: "/api/quests",
        summary: "Create a quest / Quest erstellen",
        security: [["sanctum" => []]],
        tags: ["Quests"],
        requestBody: new OA\RequestBody(
            required: true,
            content: new OA\JsonContent(ref: "#/components/schemas/Quest")
        ),
        responses: [
            new OA\Response(
                response: 201,
                description: "Created quest / Erstellte Quest",
                content: new OA\JsonContent(ref: "#/components/schemas/Quest")
            )
        ]
    )]
    public function store(Request $request)
    {
        $validated = $request->validate([
            'uuid' => 'nullable|uuid',
            'title' => 'required|string|max:255',
            'notes' => 'nullable|string',
            'difficulty' => 'required|string',
            'due_date' => 'nullable|date',
            'lock_deletion' => 'boolean',
            'done' => 'boolean',
            'category_id' => 'nullable|exists:quest_categories,id',
            'sub_quests' => 'nullable|array',
            'sub_quests.*.uuid' => 'nullable|uuid',
            'sub_quests.*.text' => 'required|string',
            'sub_quests.*.is_done' => 'boolean',
            'sub_quests.*.order_index' => 'integer',
        ]);

        if (empty($validated['uuid'])) {
            $validated['uuid'] = (string) \Illuminate\Support\Str::uuid();
        }

        $quest = $request->user()->quests()->create($validated);

        if (isset($validated['sub_quests'])) {
            foreach ($validated['sub_quests'] as $subQuestData) {
                if (empty($subQuestData['uuid'])) {
                    $subQuestData['uuid'] = (string) \Illuminate\Support\Str::uuid();
                }
                $quest->subQuests()->create($subQuestData);
            }
        }

        return $quest->load('subQuests');
    }

    #[OA\Get(
        path: "/api/quests/{id}",
        summary: "Get a specific quest / Bestimmte Quest abrufen",
        security: [["sanctum" => []]],
        tags: ["Quests"],
        parameters: [
            new OA\Parameter(
                name: "id",
                in: "path",
                required: true,
                schema: new OA\Schema(type: "integer")
            )
        ],
        responses: [
            new OA\Response(
                response: 200,
                description: "Quest details / Quest-Details",
                content: new OA\JsonContent(ref: "#/components/schemas/Quest")
            ),
            new OA\Response(response: 403, description: "Unauthorized / Nicht autorisiert")
        ]
    )]
    public function show(Request $request, Quest $quest)
    {
        if ($quest->user_id !== $request->user()->id) {
            return response()->json(['message' => 'Unauthorized'], 403);
        }
        return $quest->load('subQuests');
    }

    #[OA\Put(
        path: "/api/quests/{id}",
        summary: "Update a quest / Quest aktualisieren",
        security: [["sanctum" => []]],
        tags: ["Quests"],
        parameters: [
            new OA\Parameter(
                name: "id",
                in: "path",
                required: true,
                schema: new OA\Schema(type: "integer")
            )
        ],
        requestBody: new OA\RequestBody(
            required: true,
            content: new OA\JsonContent(ref: "#/components/schemas/Quest")
        ),
        responses: [
            new OA\Response(
                response: 200,
                description: "Updated quest / Aktualisierte Quest",
                content: new OA\JsonContent(ref: "#/components/schemas/Quest")
            )
        ]
    )]
    public function update(Request $request, Quest $quest)
    {
        if ($quest->user_id !== $request->user()->id) {
            return response()->json(['message' => 'Unauthorized'], 403);
        }

        $validated = $request->validate([
            'title' => 'string|max:255',
            'notes' => 'nullable|string',
            'difficulty' => 'string',
            'due_date' => 'nullable|date',
            'lock_deletion' => 'boolean',
            'done' => 'boolean',
            'category_id' => 'nullable|exists:quest_categories,id',
            'sub_quests' => 'nullable|array',
            'sub_quests.*.id' => 'nullable|integer',
            'sub_quests.*.uuid' => 'nullable|uuid',
            'sub_quests.*.text' => 'required|string',
            'sub_quests.*.is_done' => 'boolean',
            'sub_quests.*.order_index' => 'integer',
        ]);

        $quest->update($validated);

        if (isset($validated['sub_quests'])) {
            $keepIds = [];
            foreach ($validated['sub_quests'] as $subQuestData) {
                if (isset($subQuestData['id'])) {
                    $subQuest = $quest->subQuests()->find($subQuestData['id']);
                    if ($subQuest) {
                        $subQuest->update($subQuestData);
                        $keepIds[] = $subQuest->id;
                    }
                } else {
                    if (empty($subQuestData['uuid'])) {
                        $subQuestData['uuid'] = (string) \Illuminate\Support\Str::uuid();
                    }
                    $newSubQuest = $quest->subQuests()->create($subQuestData);
                    $keepIds[] = $newSubQuest->id;
                }
            }
            $quest->subQuests()->whereNotIn('id', $keepIds)->delete();
        }

        return $quest->load('subQuests');
    }

    #[OA\Delete(
        path: "/api/quests/{id}",
        summary: "Delete a quest / Quest löschen",
        security: [["sanctum" => []]],
        tags: ["Quests"],
        parameters: [
            new OA\Parameter(
                name: "id",
                in: "path",
                required: true,
                schema: new OA\Schema(type: "integer")
            )
        ],
        responses: [
            new OA\Response(response: 204, description: "Deleted / Gelöscht"),
            new OA\Response(response: 403, description: "Unauthorized / Nicht autorisiert")
        ]
    )]
    public function destroy(Request $request, Quest $quest)
    {
        if ($quest->user_id !== $request->user()->id) {
            return response()->json(['message' => 'Unauthorized'], 403);
        }

        $quest->delete();

        return response()->json(null, 204);
    }

    public function sync(Request $request)
    {
        $user = $request->user();

        foreach ($request->changes as $change) {
            $quest = Quest::withTrashed()->updateOrCreate(
                ['uuid' => $change['uuid'], 'user_id' => $user->id],
                [
                    'title' => $change['title'],
                    'difficulty' => $change['difficulty'],
                    'updated_at' => $change['updated_at'],
                    'deleted_at' => $change['deleted_at']
                ]
            );
        }

        $serverChanges = Quest::withTrashed()
            ->where('user_id', $user->id)
            ->where('updated_at', '>', $request->last_sync_timestamp)
            ->get();

        return response()->json(['server_changes' => $serverChanges]);
    }
}
