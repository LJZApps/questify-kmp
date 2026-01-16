<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\User;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Http;
use OpenApi\Attributes as OA;

class AuthController extends Controller
{
    #[OA\Post(
        path: "/api/auth/omrix-login",
        description: "Exchange an Omrix access token for a Questify API token. / Tauscht ein Omrix Access Token gegen ein Questify API Token aus.",
        summary: "Login via Omrix / Anmeldung über Omrix",
        requestBody: new OA\RequestBody(
            required: true,
            content: new OA\JsonContent(
                required: ["access_token"],
                properties: [
                    new OA\Property(property: "access_token", type: "string", example: "omrix_token_here")
                ]
            )
        ),
        tags: ["Authentication"],
        responses: [
            new OA\Response(
                response: 200,
                description: "Success / Erfolg",
                content: new OA\JsonContent(
                    properties: [
                        new OA\Property(property: "token", type: "string"),
                        new OA\Property(
                            property: "player_profile",
                            properties: [
                                new OA\Property(property: "username", type: "string", nullable: true),
                                new OA\Property(property: "level", type: "integer"),
                                new OA\Property(property: "needs_onboarding", type: "boolean")
                            ],
                            type: "object"
                        )
                    ]
                )
            ),
            new OA\Response(response: 401, description: "Unauthorized / Nicht autorisiert")
        ]
    )]
    public function login(Request $request)
    {
        $request->validate([
            'access_token' => 'required|string',
        ]);

        $omrixUserResponse = Http::withToken($request->access_token)
            ->get(config('services.omrix.base_url') . '/api/user/profile');

        if ($omrixUserResponse->failed()) {
            return response()->json(['message' => 'Invalid Omrix token'], 401);
        }

        $omrixUser = $omrixUserResponse->json();

        $user = User::updateOrCreate(
            ['omrix_sub' => $omrixUser['id']],
            [
                'real_name' => $omrixUser['name'],
                'email' => $omrixUser['email'],
                'avatar_url' => $omrixUser['avatar_url'] ?? null,
            ]
        );

        $isNewPlayer = empty($user->username);
        $token = $user->createToken('android-app')->plainTextToken;

        return response()->json([
            'token' => $token,
            'player_profile' => [
                'id' => $user->id,
                'username' => $user->username,
                'level' => $user->level,
                'needs_onboarding' => $isNewPlayer
            ]
        ]);
    }
}
