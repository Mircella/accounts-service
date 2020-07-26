package kz.mircella.accounts.infrastructure.jetty

import mu.KLogging
import org.eclipse.jetty.server.LowResourceMonitor
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.StatisticsHandler
import org.eclipse.jetty.util.thread.QueuedThreadPool
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.web.embedded.jetty.JettyServerCustomizer
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory
import org.springframework.context.annotation.Configuration
import javax.annotation.PostConstruct

@Configuration
@ConditionalOnBean(JettyServletWebServerFactory::class)
@EnableConfigurationProperties(value = [JettyThreadPoolSettings::class, JettyLowResourceMonitorSettings::class])
class JettyAutoConfiguration(
        val jettyThreadPoolSettings: JettyThreadPoolSettings,
        val jettyLowResourceMonitorSettings: JettyLowResourceMonitorSettings,
        val factory: JettyServletWebServerFactory
) {

    @PostConstruct
    fun customize() {
        factory.addServerCustomizers(JettyServerCustomizer { server: Server -> jettyServerCustomizer(server) })
    }

    private fun jettyServerCustomizer(server: Server) {
        server.addBean(createQueuedThreadPool(jettyThreadPoolSettings))
        server.addBean(createLowResourceMonitor(server, jettyLowResourceMonitorSettings))
        val statisticsHandler = StatisticsHandler()
        statisticsHandler.handler = server.handler
        server.handler = statisticsHandler
//        JettyStatisticsMetrics.monitor(Metrics.globalRegistry, statisticsHandler)
    }

    private fun createQueuedThreadPool(settings: JettyThreadPoolSettings): QueuedThreadPool { // TODO: add metrics for thread pool: https://github.com/micrometer-metrics/micrometer/issues/557
        val threadPool = QueuedThreadPool()
        threadPool.maxThreads = settings.maxThreads
        threadPool.minThreads = settings.minThreads
        threadPool.idleTimeout = settings.idleTimeout
        threadPool.isDetailedDump = settings.detailedDump
        threadPool.name = "HttpServer"
        logger.info("Creating embedded jetty with custom thread pool: min={}, max={}",
                settings.minThreads, settings.maxThreads)
        return threadPool
    }

    private fun createLowResourceMonitor(server: Server, settings: JettyLowResourceMonitorSettings): LowResourceMonitor {
        val lowResourcesMonitor = LowResourceMonitor(server)
        lowResourcesMonitor.period = settings.period
        lowResourcesMonitor.lowResourcesIdleTimeout = settings.idleTimeout
        lowResourcesMonitor.monitorThreads = settings.monitorThreads
        lowResourcesMonitor.maxConnections = settings.maxConnections
        lowResourcesMonitor.maxMemory = settings.maxMemory
        lowResourcesMonitor.maxLowResourcesTime = settings.maxLowResourcesTime
        return lowResourcesMonitor
    }

    private companion object : KLogging()
}
