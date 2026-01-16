<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;
use Illuminate\Database\Eloquent\Relations\HasMany;
use Illuminate\Database\Eloquent\SoftDeletes;
use OpenApi\Attributes as OA;

#[OA\Schema(
    schema: "QuestCategory",
    title: "QuestCategory",
    description: "Quest Category model",
    properties: [
        new OA\Property(property: "id", type: "integer", readOnly: true, example: 1),
        new OA\Property(property: "uuid", type: "string", format: "uuid", example: "550e8400-e29b-41d4-a716-446655440000"),
        new OA\Property(property: "text", type: "string", example: "Work"),
        new OA\Property(property: "created_at", type: "string", format: "date-time", readOnly: true),
        new OA\Property(property: "updated_at", type: "string", format: "date-time", readOnly: true),
        new OA\Property(property: "deleted_at", type: "string", format: "date-time", nullable: true, readOnly: true)
    ]
)]
class QuestCategory extends Model
{
    use HasFactory, SoftDeletes;

    protected $fillable = ['uuid', 'user_id', 'text', 'updated_at', 'deleted_at'];

    public function user(): BelongsTo
    {
        return $this->belongsTo(User::class);
    }

    public function quests(): HasMany
    {
        return $this->hasMany(Quest::class, 'category_id');
    }
}
