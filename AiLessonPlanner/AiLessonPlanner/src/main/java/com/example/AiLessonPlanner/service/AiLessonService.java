package com.example.AiLessonPlanner.service;

import com.example.AiLessonPlanner.DTO.LessonRequest;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.*;

@Slf4j
@Service
public class AiLessonService {

    @Value("${app.ai.api-key:}")
    private String apiKey;

    @Value("${app.ai.model:gemini-3.8-flash}")
    private String model;

    private final RestClient restClient = RestClient.create();

    private final ObjectMapper mapper = new ObjectMapper();

    public LessonRequest generateLesson(LessonRequest request) {

        String prompt = buildPrompt(request);

        if (apiKey == null || apiKey.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Gemini API key is missing. Please configure GEMINI_API_KEY."
            );
        }

        try {
            // Build Gemini request
            Map<String, Object> part = Map.of("text", prompt);

            Map<String, Object> content = Map.of(
                    "parts", List.of(part)
            );

//            Map<String, Object> generationConfig = Map.of(
//                    "responseMimeType", "application/json",
//                    "temperature", 0.4
//            );

            Map<String, Object> generationConfig = Map.of(
                    "responseMimeType", "application/json",
                    "temperature", 0.2,
                    "maxOutputTokens", 12000
            );

            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(content),
                    "generationConfig", generationConfig
            );

            // Call Gemini API
            Map<String, Object> apiResponse = restClient.post()
                    .uri(
                            "https://generativelanguage.googleapis.com/v1beta/models/"
                                    + model + ":generateContent"
                    )
                    .header("x-goog-api-key", apiKey)
                    .header("Content-Type", "application/json")
                    .body(requestBody)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});

            if (apiResponse == null) {
                throw new IllegalStateException(
                        "Gemini returned an empty response"
                );
            }

            // Extract generated text
            String generatedText = extractGeneratedText(apiResponse);

            log.info("Gemini generated response length: {}", generatedText.length());
            log.debug("Gemini generated response: {}", generatedText);

            Map<String, Object> candidate =
                    (Map<String, Object>) ((List<?>) apiResponse.get("candidates")).get(0);

            log.info("Gemini finish reason: {}", candidate.get("finishReason"));

            // Convert generated JSON to Java Map
            Map<String, Object> generated =
                    mapper.readValue(
                            generatedText,
                            new TypeReference<Map<String, Object>>() {}
                    );

            // Extract detailed content
            Map<String, Object> detailedContent =
                    asMap(generated.get("detailedContent"));

            // Return the generated lesson in your existing DTO
            return new LessonRequest(
                    request.classNumber(),
                    request.subject(),
                    request.topic(),
                    request.duration(),
                    request.language(),

                    getStringList(generated, "learningObjectives"),
                    getString(generated, "introduction"),
                    getString(generated, "explanation"),
                    getStringList(generated, "teachingActivities"),
                    getString(generated, "practicalActivity"),
                    getStringList(generated, "assessmentQuestions"),
                    getString(generated, "homework"),

                    detailedContent
            );

        }
        catch (RestClientResponseException e) {

            log.error("Gemini HTTP status: {}", e.getStatusCode());
            log.error("Gemini error response: {}", e.getResponseBodyAsString());

            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Gemini API error: " + e.getStatusCode()
            );

        }
        catch (RestClientException e) {

            log.error("Gemini API request failed", e);

            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Unable to generate lesson from Gemini. Check your API key, model and API quota."
            );

        } catch (Exception e) {

            log.error("Failed to process Gemini lesson response", e);

            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Gemini returned an invalid lesson response."
            );
        }
    }

    private String buildPrompt(LessonRequest request) {

        return """
                You are an expert school teacher, curriculum designer,
                subject matter expert and educational content creator.

                Create a COMPLETE, DETAILED and FACTUALLY ACCURATE lesson plan.

                LESSON DETAILS:

                Class: %s
                Subject: %s
                Topic: %s
                Duration: %s minutes
                Language: %s
                Teacher's learning objectives: %s

                IMPORTANT INSTRUCTIONS:

                1. Write for the specified school class and age level.
                2. Use the requested language throughout the lesson.
                3. Explain concepts in detail, not in one-line descriptions.
                4. Include accurate definitions, facts and subject-specific terminology.
                5. Include relevant equations, formulas and units when applicable.
                6. If the topic has no equations, return an empty equations array.
                7. Give actual topic-specific examples, not generic placeholders.
                8. Include step-by-step teaching activities.
                9. Make the total teaching activity duration equal to the requested
                   lesson duration.
                10. Include practical activities appropriate for the class.
                11. Include MCQs with four options, correct answers and explanations.
                12. Include short-answer, long-answer and application questions.
                13. Make sure the answers are scientifically and academically accurate.
                14. Never invent textbook references, URLs, research papers or citations.
                15. If a fact cannot be established confidently, clearly identify
                    the uncertainty rather than inventing it.
                16. Image prompts must describe relevant educational diagrams,
                    not unrelated decorative images.
                17. Do not include Markdown fences around the JSON.
                18. Return valid JSON only.

                REQUIRED JSON STRUCTURE:

                {
                  "learningObjectives": [
                    "Detailed measurable learning objective"
                  ],
                  "introduction": "Detailed teacher introduction and engaging opening questions",
                  "explanation": "Complete topic explanation with definitions, concepts and examples",
                  "teachingActivities": [
                    "Step-by-step classroom teaching activity with timing"
                  ],
                  "practicalActivity": "Detailed practical activity with instructions",
                  "assessmentQuestions": [
                    "Topic-specific assessment question"
                  ],
                  "homework": "Detailed topic-specific homework",
                  "detailedContent": {
                    "topicDescription": "Detailed description of the topic",
                    "prerequisites": [
                      "Required prior knowledge"
                    ],
                    "keyConcepts": [
                      {
                        "heading": "Concept name",
                        "description": "Detailed and accurate explanation"
                      }
                    ],
                    "equations": [
                      {
                        "name": "Equation name",
                        "expression": "Correct equation or formula",
                        "explanation": "Explain each term and its significance"
                      }
                    ],
                    "examples": [
                      {
                        "title": "Example title",
                        "description": "Detailed relevant example"
                      }
                    ],
                    "visualAids": [
                      {
                        "title": "Diagram title",
                        "type": "diagram",
                        "caption": "What the diagram explains",
                        "imagePrompt": "Detailed prompt for a relevant educational diagram",
                        "altText": "Accessible image description"
                      }
                    ],
                    "teachingActivities": [
                      {
                        "step": 1,
                        "title": "Activity title",
                        "durationMinutes": 5,
                        "description": "Detailed teacher and student instructions"
                      }
                    ],
                    "experiments": [
                      {
                        "title": "Experiment or practical activity",
                        "objective": "Learning objective",
                        "materials": ["Required materials"],
                        "procedure": ["Step-by-step procedure"],
                        "observation": "Expected observation",
                        "conclusion": "Scientific conclusion"
                      }
                    ],
                    "questionBank": {
                      "multipleChoice": [
                        {
                          "question": "Actual topic-specific question",
                          "options": [
                            "Option A",
                            "Option B",
                            "Option C",
                            "Option D"
                          ],
                          "correctAnswer": "Correct option",
                          "explanation": "Why the answer is correct"
                        }
                      ],
                      "shortAnswer": [
                        "Short-answer question"
                      ],
                      "longAnswer": [
                        "Detailed question"
                      ],
                      "applicationQuestions": [
                        "Real-world application question"
                      ]
                    },
                    "assessmentRubric": [
                      {
                        "criteria": "Assessment criteria",
                        "description": "What the teacher evaluates",
                        "marks": 5
                      }
                    ],
                    "references": [
                      "Only genuine, identifiable educational references"
                    ]
                  }
                }

                Return substantial, topic-specific content.
                Do not merely repeat the topic name in every section.
                """.formatted(
                request.classNumber(),
                request.subject(),
                request.topic(),
                request.duration(),
                request.language(),
                request.learningObjectives() == null
                        ? "Create suitable learning objectives"
                        : request.learningObjectives()
        );
    }

    private String extractGeneratedText(
            Map<String, Object> response
    ) {

        List<?> candidates =
                (List<?>) response.get("candidates");

        if (candidates == null || candidates.isEmpty()) {
            throw new IllegalStateException(
                    "Gemini did not return any generated content"
            );
        }

        Map<?, ?> candidate = (Map<?, ?>) candidates.get(0);

        Map<?, ?> responseContent =
                (Map<?, ?>) candidate.get("content");

        if (responseContent == null) {
            throw new IllegalStateException(
                    "Gemini response content is missing"
            );
        }

        List<?> parts = (List<?>) responseContent.get("parts");

        if (parts == null || parts.isEmpty()) {
            throw new IllegalStateException(
                    "Gemini response parts are empty"
            );
        }

        for (Object item : parts) {

            Map<?, ?> part = (Map<?, ?>) item;

            Object text = part.get("text");

            if (text instanceof String generatedText
                    && !generatedText.isBlank()) {
                return generatedText;
            }
        }

        throw new IllegalStateException(
                "Gemini did not return generated text"
        );
    }

    private Map<String, Object> asMap(Object value) {

        if (!(value instanceof Map<?, ?> map)) {
            return Map.of();
        }

        Map<String, Object> result = new LinkedHashMap<>();

        map.forEach((key, item) ->
                result.put(String.valueOf(key), item)
        );

        return result;
    }

    private String getString(
            Map<String, Object> data,
            String key
    ) {

        Object value = data.get(key);

        return value instanceof String
                ? (String) value
                : "";
    }

    private List<String> getStringList(
            Map<String, Object> data,
            String key
    ) {

        Object value = data.get(key);

        if (!(value instanceof List<?> list)) {
            return List.of();
        }

        return list.stream()
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .toList();
    }
}