<?php

namespace App\Models;

// use Illuminate\Contracts\Auth\MustVerifyEmail;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Foundation\Auth\User as Authenticatable;
use Illuminate\Notifications\Notifiable;
use Laravel\Sanctum\HasApiTokens;
use OpenApi\Attributes as OA;

#[OA\Schema(
    schema: "User",
    title: "User",
    description: "User model",
    properties: [
        new OA\Property(property: "id", type: "integer", readOnly: true, example: 1),
        new OA\Property(property: "username", type: "string", example: "hero123", nullable: true),
        new OA\Property(property: "real_name", type: "string", example: "John Doe"),
        new OA\Property(property: "email", type: "string", format: "email", example: "john@example.com"),
        new OA\Property(property: "avatar_url", type: "string", format: "uri", nullable: true),
        new OA\Property(property: "level", type: "integer", example: 1),
        new OA\Property(property: "xp", type: "integer", example: 0),
        new OA\Property(property: "bio", type: "string", nullable: true),
        new OA\Property(property: "created_at", type: "string", format: "date-time", readOnly: true),
        new OA\Property(property: "updated_at", type: "string", format: "date-time", readOnly: true)
    ]
)]
class User extends Authenticatable
{
    use HasApiTokens, HasFactory, Notifiable;

    protected $fillable = [
        'omrix_sub',
        'real_name',
        'username',
        'email',
        'avatar_url',
        'level',
        'bio',
        'xp',
    ];

    protected $attributes = [
        'level' => 1,
    ];

    protected $hidden = [
        'password',
        'remember_token',
    ];

    protected function casts(): array
    {
        return [
            'email_verified_at' => 'datetime',
            'password' => 'hashed',
        ];
    }

    public function playerStats()
    {
        return $this->hasOne(PlayerStats::class);
    }

    public function quests()
    {
        return $this->hasMany(Quest::class);
    }

    public function questCategories()
    {
        return $this->hasMany(QuestCategory::class);
    }
}
