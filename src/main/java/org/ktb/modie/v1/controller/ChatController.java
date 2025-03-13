package org.ktb.modie.v1.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "웹소켓 API", description = "WebSocket을 통한 채팅 기능 제공")
@RestController
@RequestMapping("/ws/v1/meets/{meetId}/chat")
public class ChatController {

    // 채팅방 입장
    @Operation(
        summary = "채팅방 입장 (WebSocket 연결)",
        description = """
            **WebSocket 연결 후 채팅방 입장 및 이전 채팅 내역 조회**

            - **연결 방식:** `CONNECT wss://modie.com/ws/v1/meets/{meetId}/chat`
            - **인증:** `Authorization: Bearer {ACCESS_TOKEN}`
            - **성공 시:** WebSocket 연결 후 `채팅 내역 불러오기 (GET)` 요청 자동 실행
            - **서버 응답 코드:** 아래 응답 참고
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = """
                성공적으로 채팅방에 입장했습니다.
                ```json
                {
                  "success": true,
                  "data": {}
                }
                ```
            """),
        @ApiResponse(responseCode = "400", description = """
                잘못된 요청입니다. 필요한 매개변수를 확인하세요.
                ```json
                {
                  "success": false,
                  "data": {
                    "code": "C001",
                    "message": "잘못된 요청입니다. 필요한 매개변수를 확인하세요."
                  }
                }
                ```
            """),
        @ApiResponse(responseCode = "401", description = """
                인증되지 않은 사용자입니다.
                ```json
                {
                  "success": false,
                  "data": {
                    "code": "C002",
                    "message": "인증되지 않은 사용자입니다."
                  }
                }
                ```
            """),
        @ApiResponse(responseCode = "403", description = """
                로그인한 사용자만 채팅을 이용할 수 있습니다.
                ```json
                {
                  "success": false,
                  "data": {
                    "code": "C003",
                    "message": "로그인한 사용자만 채팅을 이용할 수 있습니다."
                  }
                }
                ```
            """),
        @ApiResponse(responseCode = "404", description = """
                해당 채팅을 찾을 수 없습니다.
                ```json
                {
                  "success": false,
                  "data": {
                    "code": "C004",
                    "message": "해당 채팅을 찾을 수 없습니다."
                  }
                }
                ```
            """)
    })
    @GetMapping("/info")
    public String getWebSocketInfo() {
        return "WebSocket endpoint is wss://modie.com/ws/v1/meets/{meetId}/chat";
    }

    // 채팅 보내기
    @Operation(
        summary = "채팅 보내기",
        description = """
            **WebSocket을 통해 채팅 메시지를 전송하는 기능**

            - **연결 방식:** `SEND wss://modie.com/ws/v1/meets/{meetId}/chat`
            - **인증:** `Authorization: Bearer {ACCESS_TOKEN}`
            - **전송 메시지 본문 예시:**
              ```json
              {
                "message": "안녕하세요, 여러분!",
                "meetId": 12345,
                "user_id": 1
              }
              ```
            - **서버 응답 코드:** 아래 응답 참고
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = """
                성공적으로 메시지를 보냈습니다.
                ```json
                {
                  "success": true,
                  "data": {}
                }
                ```
            """),
        @ApiResponse(responseCode = "400", description = """
                메시지는 비워둘 수 없습니다.
                ```json
                {
                  "success": false,
                  "data": {
                    "code": "C005",
                    "message": "메시지는 비워둘 수 없습니다."
                  }
                }
                ```
            """),
        @ApiResponse(responseCode = "401", description = """
                인증되지 않은 사용자입니다. 로그인 후 다시 시도해주세요.
                ```json
                {
                  "success": false,
                  "data": {
                    "code": "C006",
                    "message": "인증되지 않은 사용자입니다. 로그인 후 다시 시도해주세요."
                  }
                }
                ```
            """),
        @ApiResponse(responseCode = "403", description = """
                로그인한 사용자만 채팅을 이용할 수 있습니다.
                ```json
                {
                  "success": false,
                  "data": {
                    "code": "C007",
                    "message": "로그인한 사용자만 채팅을 이용할 수 있습니다."
                  }
                }
                ```
            """),
        @ApiResponse(responseCode = "404", description = """
                해당 채팅을 찾을 수 없습니다.
                ```json
                {
                  "success": false,
                  "data": {
                    "code": "C008",
                    "message": "해당 채팅을 찾을 수 없습니다."
                  }
                }
                ```
            """),
        @ApiResponse(responseCode = "409", description = """
                채팅방에 참여하지 않은 사용자는 메시지를 보낼 수 없습니다.
                ```json
                {
                  "success": false,
                  "data": {
                    "code": "C009",
                    "message": "채팅방에 참여하지 않은 사용자는 메시지를 보낼 수 없습니다."
                  }
                }
                ```
            """),
        @ApiResponse(responseCode = "422", description = """
                유효하지 않은 meetId입니다.
                ```json
                {
                  "success": false,
                  "data": {
                    "code": "C010",
                    "message": "유효하지 않은 meetId입니다."
                  }
                }
                ```
            """)
    })
    @GetMapping("/send")
    public String sendMessage() {
        return "Send a message to the WebSocket endpoint wss://modie.com/ws/v1/meets/{meetId}/chat";
    }

    // 채팅 수신
    @Operation(
        summary = "채팅 수신 (WebSocket 메시지 수신)",
        description = """
            **WebSocket을 통해 채팅 메시지를 실시간으로 수신하는 기능**

            - **연결 방식:** `SUBSCRIBE wss://modie.com/ws/v1/meets/{meetId}/chat`
            - **인증:** `Authorization: Bearer {ACCESS_TOKEN}`
            - **서버에서 메시지를 푸시:** 채팅방에서 새로운 메시지가 수신되면 서버에서 자동으로 푸시됩니다.
            - **서버 응답 예시:**
              ```json
              {
                "success": true,
                "data": {
                  "messages": [
                    {
                      "message_id": 1,
                      "meet_id": 12345,
                      "user_id": 1,
                      "message": "마 니 안오냐",
                      "created_at": "2025-03-10 15:24"
                    },
                    {
                      "message_id": 2,
                      "meet_id": 12345,
                      "user_id": 2,
                      "message": "가고있다 마",
                      "created_at": "2025-03-10 18:35"
                    }
                  ]
                }
              }
              ```
            - **서버 응답 코드:** 아래 응답 참고
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = """
                메시지가 성공적으로 수신되었습니다.
                ```json
                {
                  "success": true,
                  "data": {
                    "messages": [
                      {
                        "message_id": 1,
                        "meet_id": 12345,
                        "user_id": 1,
                        "message": "마 니 안오냐",
                        "created_at": "2025-03-10 15:24"
                      },
                      {
                        "message_id": 2,
                        "meet_id": 12345,
                        "user_id": 2,
                        "message": "가고있다 마",
                        "created_at": "2025-03-10 18:35"
                      }
                    ]
                  }
                }
                ```
            """),
        @ApiResponse(responseCode = "400", description = """
                잘못된 요청입니다. 올바른 형식으로 요청해주세요.
                ```json
                {
                  "success": false,
                  "data": {
                    "code": "C011",
                    "message": "잘못된 요청입니다. 올바른 형식으로 요청해주세요."
                  }
                }
                ```
            """),
        @ApiResponse(responseCode = "401", description = """
                인증되지 않은 사용자입니다. 로그인 후 다시 시도해주세요.
                ```json
                {
                  "success": false,
                  "data": {
                    "code": "C012",
                    "message": "인증되지 않은 사용자입니다. 로그인 후 다시 시도해주세요."
                  }
                }
                ```
            """),
        @ApiResponse(responseCode = "403", description = """
                해당 채팅방에 참여할 수 없습니다.
                ```json
                {
                  "success": false,
                  "data": {
                    "code": "C013",
                    "message": "해당 채팅방에 참여할 수 없습니다."
                  }
                }
                ```
            """),
        @ApiResponse(responseCode = "404", description = """
                해당 채팅방을 찾을 수 없습니다.
                ```json
                {
                  "success": false,
                  "data": {
                    "code": "C014",
                    "message": "해당 채팅방을 찾을 수 없습니다."
                  }
                }
                ```
            """),
        @ApiResponse(responseCode = "408", description = """
                요청 시간이 초과되었습니다. 다시 시도해주세요.
                ```json
                {
                  "success": false,
                  "data": {
                    "code": "C015",
                    "message": "요청 시간이 초과되었습니다. 다시 시도해주세요."
                  }
                }
                ```
            """)
    })
    @GetMapping("/subscribe")
    public String subscribeToMessages() {
        return "Subscribe to the WebSocket endpoint wss://modie.com/ws/v1/meets/{meetId}/chat for receiving messages.";
    }

    @Operation(
        summary = "채팅 내역 불러오기",
        description = """
            **채팅방 내역을 `message_id` 기준으로 불러오는 API**.

            - `lastMessageId`: 마지막으로 조회한 메시지 ID (기본값은 가장 최근 메시지부터 조회)
            - `size`: 페이지당 메시지 수 (기본값은 25, 최대값은 100)

            **요청 예시**
            ```bash
            GET /api/v1/meets/{meetId}/chat?lastMessageId=10&size=25
            Authorization: Bearer {ACCESS_TOKEN}
            ```
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = """
                채팅 내역을 성공적으로 불러왔습니다.
                ```json
                {
                  "success": true,
                  "data": {
                    "messages": [
                      {
                        "message_id": 9,
                        "meet_id": 12345,
                        "user_id": 1,
                        "message": "마 니 안오냐",
                        "created_at": "2025-03-10 15:24"
                      },
                      {
                        "message_id": 8,
                        "meet_id": 12345,
                        "user_id": 2,
                        "message": "가고있다 마",
                        "created_at": "2025-03-10 18:35"
                      }
                    ],
                    "lastMessageId": 9,
                    "size": 25
                  }
                }
                ```
            """),
        @ApiResponse(responseCode = "400", description = """
                잘못된 요청입니다. 올바른 형식으로 요청해주세요.
                ```json
                {
                  "success": false,
                  "data": {
                    "code": "C013",
                    "message": "잘못된 요청입니다. 올바른 형식으로 요청해주세요."
                  }
                }
                ```
            """),
        @ApiResponse(responseCode = "401", description = """
                인증되지 않은 사용자입니다. 로그인 후 다시 시도해주세요.
                ```json
                {
                  "success": false,
                  "data": {
                    "code": "C014",
                    "message": "인증되지 않은 사용자입니다. 로그인 후 다시 시도해주세요."
                  }
                }
                ```
            """),
        @ApiResponse(responseCode = "403", description = """
                해당 채팅방에 접근할 권한이 없습니다.
                ```json
                {
                  "success": false,
                  "data": {
                    "code": "C015",
                    "message": "해당 채팅방에 접근할 권한이 없습니다."
                  }
                }
                ```
            """),
        @ApiResponse(responseCode = "404", description = """
                해당 채팅방을 찾을 수 없습니다.
                ```json
                {
                  "success": false,
                  "data": {
                    "code": "C016",
                    "message": "해당 채팅방을 찾을 수 없습니다."
                  }
                }
                ```
            """)
    })
    @GetMapping
    public String getChatHistory(
        @PathVariable("meetId") Long meetId,  // meetId를 경로에서 받아옵니다.
        @Parameter(description = "마지막으로 조회한 메시지 ID (기본값은 가장 최근 메시지부터 조회)")
        @RequestParam(defaultValue = "0") Long lastMessageId,  // 마지막 메시지 ID
        @Parameter(description = "페이지당 메시지 수 (기본값은 25, 최대값은 100)")
        @RequestParam(defaultValue = "25") int size) {

        // 실제 로직에서는 여기서 `meetId`, `lastMessageId`, `size`를 사용해 DB에서 메시지를 조회
        // `lastMessageId`가 0이면 가장 최근 메시지부터 불러옵니다.

        return String.format("Meet ID: %d, Last Message ID: %d, Size: %d로 채팅 내역을 조회합니다.", meetId, lastMessageId, size);
    }
}
