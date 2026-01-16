<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Http\Requests\ProfileUpdateRequest;
use App\Http\Resources\UserResource;
use App\Models\User;
use Illuminate\Http\Request;
use OpenApi\Attributes as OA;

class ProfileController extends Controller
{
    #[OA\Get(
        path: "/api/profile",
        summary: "Get own profile / Eigenes Profil abrufen",
        security: [["sanctum" => []]],
        tags: ["Profile"],
        responses: [
            new OA\Response(
                response: 200,
                description: "User profile data / Benutzerprofildaten",
                content: new OA\JsonContent(ref: "#/components/schemas/User")
            )
        ]
    )]
    public function show(Request $request)
    {
        return new UserResource($request->user());
    }

    #[OA\Put(
        path: "/api/profile",
        summary: "Update profile / Profil aktualisieren",
        security: [["sanctum" => []]],
        tags: ["Profile"],
        requestBody: new OA\RequestBody(
            required: true,
            content: new OA\JsonContent(
                properties: [
                    new OA\Property(property: "username", type: "string", example: "hero123"),
                    new OA\Property(property: "real_name", type: "string", example: "John Doe"),
                    new OA\Property(property: "bio", type: "string", example: "I love quests!")
                ]
            )
        ),
        responses: [
            new OA\Response(
                response: 200,
                description: "Updated profile / Aktualisiertes Profil",
                content: new OA\JsonContent(ref: "#/components/schemas/User")
            )
        ]
    )]
    public function update(ProfileUpdateRequest $request)
    {
        $user = $request->user();
        $user->update($request->validated());

        return new UserResource($user);
    }

    #[OA\Get(
        path: "/api/profile/check-username",
        summary: "Check username availability / Verfügbarkeit des Benutzernamens prüfen",
        security: [["sanctum" => []]],
        tags: ["Profile"],
        parameters: [
            new OA\Parameter(
                name: "username",
                in: "query",
                required: true,
                schema: new OA\Schema(type: "string")
            )
        ],
        responses: [
            new OA\Response(
                response: 200,
                description: "Availability status / Verfügbarkeitsstatus",
                content: new OA\JsonContent(
                    properties: [
                        new OA\Property(property: "available", type: "boolean")
                    ]
                )
            )
        ]
    )]
    public function checkUsername(Request $request)
    {
        $request->validate(['username' => 'required|string']);

        $exists = User::where('username', $request->username)
            ->where('id', '!=', $request->user()?->id)
            ->exists();

        return response()->json([
            'available' => !$exists
        ]);
    }
}
