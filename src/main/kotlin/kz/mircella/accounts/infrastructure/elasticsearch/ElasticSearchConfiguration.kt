package kz.mircella.accounts.infrastructure.elasticsearch

import org.apache.http.HttpHost
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.CredentialsProvider
import org.apache.http.impl.client.BasicCredentialsProvider
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestHighLevelClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class ElasticSearchConfiguration {

    @Bean
    fun restHighLevelClient(@Value("\${elasticsearch.host}") host: String,
                            @Value("\${elasticsearch.port}") port: Int,
                            @Value("\${elasticsearch.connect-timeout}") connectTimeout: Int,
                            @Value("\${elasticsearch.request-timeout}") requestTimeout: Int): RestHighLevelClient {
        val credentialsProvider: CredentialsProvider = BasicCredentialsProvider()
        credentialsProvider.setCredentials(AuthScope.ANY, UsernamePasswordCredentials("paf8nuy6r1", "rv5fk73wlj"))
        return RestHighLevelClient(
                RestClient.builder(HttpHost(host, port, "https"))
                        .setRequestConfigCallback {
                            it.setConnectTimeout(connectTimeout).setConnectionRequestTimeout(requestTimeout)
                        }
                        .setHttpClientConfigCallback {
                            it.disableAuthCaching()
                            it.setDefaultCredentialsProvider(credentialsProvider)
                        })
    }
}