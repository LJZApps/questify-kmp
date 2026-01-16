<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;
use Illuminate\Database\Eloquent\SoftDeletes;
use OpenApi\Attributes as OA;

#[OA\Schema(
    schema: "SubQuest",
    title: "SubQuest",
    description: "SubQuest model",
    properties: [
        new OA\Property(property: "id", type: "integer", readOnly: true, example: 1),
        new OA\Property(property: "uuid", type: "string", format: "uuid", example: "550e8400-e29b-41d4-a716-446655440000"),
        new OA\Property(property: "text", type: "string", example: "Buy milk"),
        new OA\Property(property: "is_done", type: "boolean", example: false),
        new OA\Property(property: "order_index", type: "integer", example: 0),
        new OA\Property(property: "created_at", type: "string", format: "date-time", readOnly: true),
        new OA\Property(property: "updated_at", type: "string", format: "date-time", readOnly: true),
        new OA\Property(property: "deleted_at", type: "string", format: "date-time", nullable: true, readOnly: true)
    ]
)]
class SubQuest extends Model
{
    use HasFactory, SoftDeletes;

    protected $fillable = [
        'uuid',
        'quest_id',
        'text',
        'is_done',
        'order_index',
        'updated_at',
        'deleted_at'
    ];

    protected $casts = [
        'is_done' => 'boolean',
    ];

    public function quest(): BelongsTo
    {
        return $this->belongsTo(Quest::class);
    }
}
