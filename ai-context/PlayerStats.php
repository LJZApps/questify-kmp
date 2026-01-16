<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;
use OpenApi\Attributes as OA;

#[OA\Schema(
    schema: "PlayerStats",
    title: "PlayerStats",
    description: "Player Stats model",
    properties: [
        new OA\Property(property: "id", type: "integer", readOnly: true, example: 1),
        new OA\Property(property: "level", type: "integer", example: 1),
        new OA\Property(property: "xp", type: "integer", example: 0),
        new OA\Property(property: "points", type: "integer", example: 0),
        new OA\Property(property: "current_hp", type: "integer", example: 100),
        new OA\Property(property: "max_hp", type: "integer", example: 100),
        new OA\Property(property: "status", type: "string", example: "NORMAL"),
        new OA\Property(property: "status_expiry_timestamp", type: "integer", nullable: true)
    ]
)]
class PlayerStats extends Model
{
    use HasFactory;
    protected $fillable = [
        'user_id',
        'level',
        'xp',
        'points',
        'current_hp',
        'max_hp',
        'status',
        'status_expiry_timestamp'
    ];

    public function user(): BelongsTo
    {
        return $this->belongsTo(User::class);
    }
}
