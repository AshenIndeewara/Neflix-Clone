package lk.ijse.netflix.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.ijse.netflix.dto.UserData;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.util.Base64;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ClerkAuthFilter extends OncePerRequestFilter {
    @Value("${clerk.pem.public.key}")
    private String CLERK_PEM_PUBLIC_KEY;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("No Authorization header or it does not start with Bearer");
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        System.out.println("Received token: " + token);

        try {
            PublicKey publicKey = getClerkPublicKey();

            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token);

            Claims body = claims.getBody();

//            System.out.println("JWT Claims:");
//            for (String key : body.keySet()) {
//                System.out.println(key + " : " + body.get(key));
//            }

            String userId = body.getSubject();
            //System.out.println("User ID from token: " + userId);

            String email = body.get("email", String.class);
            //System.out.println("Email from token: " + email);

            String role = body.get("role", String.class);
            System.out.println("Role from token: " + role);

            String fullName = body.get("fullName", String.class);
            String userImage = body.get("userImage", String.class);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            new UserData(
                                    userId,
                                    fullName,
                                    email,
                                    userImage
                            ),
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + role))
                    );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to parse JWT or unauthorized");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private PublicKey getClerkPublicKey() throws Exception {
        // Method 1: If you have the PEM key
        String publicKeyPEM = CLERK_PEM_PUBLIC_KEY
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(publicKeyPEM);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(new java.security.spec.X509EncodedKeySpec(decoded));

    }
}