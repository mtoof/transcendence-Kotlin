package transcendence.user_service.config

import io.r2dbc.pool.ConnectionPool
import io.r2dbc.pool.ConnectionPoolConfiguration
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration
import io.r2dbc.postgresql.PostgresqlConnectionFactory
import io.r2dbc.spi.ConnectionFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.r2dbc.connection.init.CompositeDatabasePopulator
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator
import java.time.Duration

@Configuration
@EnableR2dbcRepositories(basePackages = ["transcendence.user_service.repository"])
class DatabaseConfig : AbstractR2dbcConfiguration() {

    @Bean
    override fun connectionFactory(): ConnectionFactory {
        val config = PostgresqlConnectionConfiguration.builder()
            .host("db")
            .database("testDb")
            .username("someUser")
            .password("somePassword")
            .build()

        val connectionFactory = PostgresqlConnectionFactory(config)

        // Configure the connection pool
        val poolConfig = ConnectionPoolConfiguration.builder(connectionFactory)
            .maxIdleTime(Duration.ofMinutes(30))  // Optional configuration for idle connections
            .maxSize(20)  // Maximum pool size
            .minIdle(5)   // Minimum idle connections
            .build()

        return ConnectionPool(poolConfig)
    }

    @Bean
    fun databaseInitializer(connectionFactory: ConnectionFactory): ConnectionFactoryInitializer =
        ConnectionFactoryInitializer().apply {
            setConnectionFactory(connectionFactory)
            setDatabasePopulator(
                CompositeDatabasePopulator().apply {
                    addPopulators(
                        ResourceDatabasePopulator(
                            ClassPathResource("sql/schema.sql")
                        )
                    )
                }
            )
        }
}