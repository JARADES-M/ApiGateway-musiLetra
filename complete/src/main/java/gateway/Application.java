package gateway;

import reactor.core.publisher.Mono;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableConfigurationProperties(UriConfiguration.class)
@RestController
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder, UriConfiguration uriConfiguration) {
        
        return builder.routes()
        		
            .route(p -> p.path("/musiletra")
            		.filters(f -> f
            				.hystrix(config -> config
            						.setFallbackUri(uriConfiguration.getFallbackUri())
            						)
            				)
            		.uri(uriConfiguration.getMusiLetra())
            		)
            		
            .route(p -> p .path("/spotify/auth")
            		.filters(f -> f
            				.hystrix(config -> config
            						.setFallbackUri(uriConfiguration.getFallbackUri()))
            				)
            		.uri(uriConfiguration.getSpotifyAuth())
            		)
            
            .route(p -> p .path("/musixm/auth")
            		.filters(f -> f
            				.hystrix(config -> config
            						.setFallbackUri(uriConfiguration.getFallbackUri())
            						)
            				)
            		.uri(uriConfiguration.getMusixmAuth())
            		)
            
            .build();
    }

    @RequestMapping("/fallback")
    public Mono<String> fallback() {
        return Mono.just("Erro em tentativa de requisição!");
    }

}

@ConfigurationProperties
class UriConfiguration {
    
        final String musiLetra = "http://localhost:80";
        final String spotifyAuth = "http://localhost:8888";
        final String musixmAuth = "http://localhost:7777";
        final String fallbackUri = "forward:/fallback";

        public String getMusiLetra() {
			return musiLetra;
		}
		public String getSpotifyAuth() {
			return spotifyAuth;
		}
		public String getMusixmAuth() {
			return musixmAuth;
		}
		public String getFallbackUri() {
			return fallbackUri;
		}

}