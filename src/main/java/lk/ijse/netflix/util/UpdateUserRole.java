package lk.ijse.netflix.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;

@Component
@RequiredArgsConstructor
public class UpdateUserRole {

//    @Value("${clerk.secretKey}")
//    private String secretKey;
//
//    public String updateUserRole(String role, String user) throws IOException, InterruptedException {
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create("https://api.clerk.com/v1/users/%7Buser_id%7D/metadata"))
//                .header("Content-Type", "application/json")
//                .header("Authorization", "Bearer "+secretKey)
//                .method("PATCH", HttpRequest.BodyPublishers.ofString("{\"public_metadata\":{\"propertyName*\":\"anything\"},\"private_metadata\":{\"propertyName*\":\"anything\"},\"unsafe_metadata\":{\"propertyName*\":\"anything\"}}"))
//                .build();
//        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
//        System.out.println(response.body());
//        return response.body();
//    }
}
