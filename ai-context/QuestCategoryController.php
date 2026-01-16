<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\QuestCategory;
use Illuminate\Http\Request;
use OpenApi\Attributes as OA;

class QuestCategoryController extends Controller
{
    #[OA\Get(
        path: "/api/quest-categories",
        summary: "List all quest categories / Alle Quest-Kategorien auflisten",
        security: [["sanctum" => []]],
        tags: ["Quest Categories"],
        responses: [
            new OA\Response(
                response: 200,
                description: "List of categories / Liste der Kategorien",
                content: new OA\JsonContent(type: "array", items: new OA\Items(ref: "#/components/schemas/QuestCategory"))
            )
        ]
    )]
    public function index(Request $request)
    {
        return $request->user()->questCategories;
    }

    #[OA\Post(
        path: "/api/quest-categories",
        summary: "Create a quest category / Quest-Kategorie erstellen",
        security: [["sanctum" => []]],
        tags: ["Quest Categories"],
        requestBody: new OA\RequestBody(
            required: true,
            content: new OA\JsonContent(ref: "#/components/schemas/QuestCategory")
        ),
        responses: [
            new OA\Response(
                response: 201,
                description: "Created category / Erstellte Kategorie",
                content: new OA\JsonContent(ref: "#/components/schemas/QuestCategory")
            )
        ]
    )]
    public function store(Request $request)
    {
        $validated = $request->validate([
            'uuid' => 'nullable|uuid',
            'text' => 'required|string|max:255',
        ]);

        if (empty($validated['uuid'])) {
            $validated['uuid'] = (string) \Illuminate\Support\Str::uuid();
        }

        return $request->user()->questCategories()->create($validated);
    }

    #[OA\Get(
        path: "/api/quest-categories/{id}",
        summary: "Get a quest category / Quest-Kategorie abrufen",
        security: [["sanctum" => []]],
        tags: ["Quest Categories"],
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
                description: "Category details / Kategorie-Details",
                content: new OA\JsonContent(ref: "#/components/schemas/QuestCategory")
            )
        ]
    )]
    public function show(Request $request, QuestCategory $questCategory)
    {
        if ($questCategory->user_id !== $request->user()->id) {
            return response()->json(['message' => 'Unauthorized'], 403);
        }
        return $questCategory;
    }

    #[OA\Put(
        path: "/api/quest-categories/{id}",
        summary: "Update a quest category / Quest-Kategorie aktualisieren",
        security: [["sanctum" => []]],
        tags: ["Quest Categories"],
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
            content: new OA\JsonContent(ref: "#/components/schemas/QuestCategory")
        ),
        responses: [
            new OA\Response(
                response: 200,
                description: "Updated category / Aktualisierte Kategorie",
                content: new OA\JsonContent(ref: "#/components/schemas/QuestCategory")
            )
        ]
    )]
    public function update(Request $request, QuestCategory $questCategory)
    {
        if ($questCategory->user_id !== $request->user()->id) {
            return response()->json(['message' => 'Unauthorized'], 403);
        }

        $validated = $request->validate([
            'text' => 'required|string|max:255',
        ]);

        $questCategory->update($validated);

        return $questCategory;
    }

    #[OA\Delete(
        path: "/api/quest-categories/{id}",
        summary: "Delete a quest category / Quest-Kategorie löschen",
        security: [["sanctum" => []]],
        tags: ["Quest Categories"],
        parameters: [
            new OA\Parameter(
                name: "id",
                in: "path",
                required: true,
                schema: new OA\Schema(type: "integer")
            )
        ],
        responses: [
            new OA\Response(response: 204, description: "Deleted / Gelöscht")
        ]
    )]
    public function destroy(Request $request, QuestCategory $questCategory)
    {
        if ($questCategory->user_id !== $request->user()->id) {
            return response()->json(['message' => 'Unauthorized'], 403);
        }

        $questCategory->delete();

        return response()->json(null, 204);
    }
}
