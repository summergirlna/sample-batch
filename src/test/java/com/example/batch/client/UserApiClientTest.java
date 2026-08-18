package com.example.batch.client;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.example.batch.client.response.UserResponse;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

@ExtendWith(MockitoExtension.class)
class UserApiClientTest {

  @Test
  void listByIds() {
    RestClient.Builder builder = RestClient.builder().baseUrl("http://localhost");
    MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();

    UserApiClient userApiClient = new UserApiClient(builder.build());

    server
        .expect(once(), requestTo("http://localhost/api/users/search"))
        .andExpect(method(POST))
        .andExpect(
            content()
                .json(
                    """
                        {
                          "userIds": ["1", "2"]
                        }
                        """))
        .andRespond(
            withSuccess(
                """
                        [
                          {
                            "id": "1",
                            "name": "test1"
                          },
                          {
                            "id": "2",
                            "name": "test2"
                          }
                        ]
                        """,
                MediaType.APPLICATION_JSON));

    List<UserResponse> actual = userApiClient.listByIds(List.of("1", "2"));

    assertEquals(2, actual.size());
    assertEquals("1", actual.getFirst().id());
    assertEquals("test1", actual.getFirst().name());
    assertEquals("2", actual.get(1).id());
    assertEquals("test2", actual.get(1).name());

    server.verify();
  }
}
