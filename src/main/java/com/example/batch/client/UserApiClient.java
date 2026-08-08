package com.example.batch.client;

import com.example.batch.client.request.ListByUserIdsRequest;
import com.example.batch.client.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserApiClient {

    private final RestClient restClient;

    public List<UserResponse> listByIds(List<String> ids) {
        return restClient.post()
                .uri("/api/users/search")
                .body(new ListByUserIdsRequest(ids))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

}
