<?php

namespace App\Http\Requests;

use Illuminate\Foundation\Http\FormRequest;

use Illuminate\Validation\Rule;

class ProfileUpdateRequest extends FormRequest
{
    /**
     * Determine if the user is authorized to make this request.
     */
    public function authorize(): bool
    {
        return true;
    }

    /**
     * Get the validation rules that apply to the request.
     *
     * @return array<string, \Illuminate\Contracts\Validation\ValidationRule|array<mixed>|string>
     */
    public function rules(): array
    {
        $user = $this->user();

        return [
            'username' => [
                $user->username ? 'nullable' : 'required',
                'string',
                'min:3',
                'max:20',
                'alpha_dash',
                Rule::unique('users')->ignore($user->id),
            ],
            'bio' => ['nullable', 'string', 'max:500'],
        ];
    }
}
