package br.ufmg.dcc.speed.lemonade.serving;

import com.codahale.metrics.health.HealthCheck;

public class AppHealthCheck extends HealthCheck {
    private String status;

    public AppHealthCheck(String status) {
        this.status = status;
    }

    @Override
    protected Result check() throws Exception {
        return ("OK".equals(status))
                ? Result.healthy(status)
                : Result.unhealthy(status);
    }
}
