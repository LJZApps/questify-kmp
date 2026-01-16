<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use OpenApi\Attributes as OA;

class PlayerStatsController extends Controller
{
    #[OA\Get(
        path: "/api/player-stats",
        summary: "Get player stats / Spielerstatistiken abrufen",
        security: [["sanctum" => []]],
        tags: ["Player Stats"],
        responses: [
            new OA\Response(
                response: 200,
                description: "Player statistics / Spielerstatistiken",
                content: new OA\JsonContent(ref: "#/components/schemas/PlayerStats")
            )
        ]
    )]
    public function show(Request $request)
    {
        $stats = $request->user()->playerStats;
        if (!$stats) {
            $stats = $request->user()->playerStats()->create([
                'level' => 1,
                'xp' => 0,
                'points' => 0,
                'current_hp' => 100,
                'max_hp' => 100,
                'status' => 'NORMAL',
            ]);
        }
        return $stats;
    }

    #[OA\Put(
        path: "/api/player-stats",
        summary: "Update player stats / Spielerstatistiken aktualisieren",
        security: [["sanctum" => []]],
        tags: ["Player Stats"],
        requestBody: new OA\RequestBody(
            required: true,
            content: new OA\JsonContent(ref: "#/components/schemas/PlayerStats")
        ),
        responses: [
            new OA\Response(
                response: 200,
                description: "Updated statistics / Aktualisierte Statistiken",
                content: new OA\JsonContent(ref: "#/components/schemas/PlayerStats")
            )
        ]
    )]
    public function update(Request $request)
    {
        $stats = $request->user()->playerStats;

        $validated = $request->validate([
            'level' => 'integer',
            'xp' => 'integer',
            'points' => 'integer',
            'current_hp' => 'integer',
            'max_hp' => 'integer',
            'status' => 'string',
            'status_expiry_timestamp' => 'nullable|integer',
        ]);

        if (!$stats) {
            $stats = $request->user()->playerStats()->create($validated);
        } else {
            $stats->update($validated);
        }

        return $stats;
    }
}
