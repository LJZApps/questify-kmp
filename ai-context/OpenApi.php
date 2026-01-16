<?php

namespace App\Http\Controllers\Api;

use OpenApi\Attributes as OA;

#[OA\Info(
    title: "Questify API",
    version: "1.0.0",
    description: "API Documentation for Questify Synchronization. / API-Dokumentation für die Questify-Synchronisation.",
    contact: new OA\Contact(email: "support@questify.example.com")
)]
#[OA\Server(
    url: "http://localhost",
    description: "Questify API Server"
)]
#[OA\SecurityScheme(
    securityScheme: "sanctum",
    type: "apiKey",
    description: "Enter token in format (Bearer <token>) / Geben Sie den Token im Format (Bearer <token>) ein",
    name: "Authorization",
    in: "header"
)]
class OpenApi
{
}
