package com.example.batch.client;

import com.example.batch.client.request.ListByUserIdsRequest;
import com.example.batch.client.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class UserApiClient {

    private static final String USERS_SEARCH_PATH = "/api/users/search";
    private static final ParameterizedTypeReference<List<UserResponse>> USER_RESPONSE_LIST_TYPE =
            new ParameterizedTypeReference<>() {};

    private final RestClient restClient;

    public List<UserResponse> listByIds(List<String> ids) {
        List<UserResponse> response = restClient.post()
                .uri(USERS_SEARCH_PATH)
                .body(new ListByUserIdsRequest(ids))
                .retrieve()
                .body(USER_RESPONSE_LIST_TYPE);

        return Objects.requireNonNullElse(response, List.of());
    }

}
