<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;
use Illuminate\Database\Eloquent\Relations\HasMany;
use Illuminate\Database\Eloquent\SoftDeletes;
use OpenApi\Attributes as OA;

#[OA\Schema(
    schema: "Quest",
    title: "Quest",
    description: "Quest model",
    properties: [
        new OA\Property(property: "id", type: "integer", readOnly: true, example: 1),
        new OA\Property(property: "uuid", type: "string", format: "uuid", example: "550e8400-e29b-41d4-a716-446655440000"),
        new OA\Property(property: "category_id", type: "integer", nullable: true, example: 1),
        new OA\Property(property: "title", type: "string", example: "Buy groceries"),
        new OA\Property(property: "notes", type: "string", nullable: true, example: "Milk, Bread, Eggs"),
        new OA\Property(property: "difficulty", type: "string", example: "EASY"),
        new OA\Property(property: "due_date", type: "string", format: "date-time", nullable: true),
        new OA\Property(property: "lock_deletion", type: "boolean", example: false),
        new OA\Property(property: "done", type: "boolean", example: false),
        new OA\Property(property: "sub_quests", type: "array", items: new OA\Items(ref: "#/components/schemas/SubQuest")),
        new OA\Property(property: "created_at", type: "string", format: "date-time", readOnly: true),
        new OA\Property(property: "updated_at", type: "string", format: "date-time", readOnly: true),
        new OA\Property(property: "deleted_at", type: "string", format: "date-time", nullable: true, readOnly: true)
    ]
)]
class Quest extends Model
{
    use HasFactory, SoftDeletes;

    protected $fillable = [
        'uuid',
        'user_id',
        'category_id',
        'title',
        'notes',
        'difficulty',
        'due_date',
        'lock_deletion',
        'done',
        'updated_at',
        'deleted_at'
    ];

    protected $casts = [
        'due_date' => 'datetime',
        'lock_deletion' => 'boolean',
        'done' => 'boolean',
    ];

    public function user(): BelongsTo
    {
        return $this->belongsTo(User::class);
    }

    public function category(): BelongsTo
    {
        return $this->belongsTo(QuestCategory::class, 'category_id');
    }

    public function subQuests(): HasMany
    {
        return $this->hasMany(SubQuest::class);
    }
}
