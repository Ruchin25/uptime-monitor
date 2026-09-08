package com.site.uptime.domain;

/** How a target is probed. HTTP performs a GET; TCP opens a socket to host:port. */
public enum MonitorType {
    HTTP,
    TCP
}
