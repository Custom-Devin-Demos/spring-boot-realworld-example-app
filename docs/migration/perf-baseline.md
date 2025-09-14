# Performance Baseline Documentation

## Overview
This document captures performance baselines for the Spring Boot RealWorld application before Java migration from Java 11 to Java 24. These metrics will be used to ensure no performance regressions during the migration process.

## Baseline Environment
- **Java Version**: OpenJDK 11
- **Spring Boot Version**: 2.6.3
- **Database**: SQLite 3.36.0.3
- **Test Date**: TBD (to be populated after running baseline scripts)

## API Endpoint Baselines

### Critical Endpoints (5% regression threshold)
| Endpoint | Method | Avg Response Time | 95th Percentile | Throughput (req/s) |
|----------|--------|-------------------|-----------------|-------------------|
| `/articles` | GET | TBD ms | TBD ms | TBD |
| `/articles/feed` | GET | TBD ms | TBD ms | TBD |
| `/tags` | GET | TBD ms | TBD ms | TBD |
| `/profiles/{username}` | GET | TBD ms | TBD ms | TBD |
| `/users/login` | POST | TBD ms | TBD ms | TBD |

### Secondary Endpoints (10% regression threshold)
| Endpoint | Method | Avg Response Time | 95th Percentile | Throughput (req/s) |
|----------|--------|-------------------|-----------------|-------------------|
| `/articles` | POST | TBD ms | TBD ms | TBD |
| `/articles/{slug}/comments` | GET | TBD ms | TBD ms | TBD |
| `/articles/{slug}/favorite` | POST | TBD ms | TBD ms | TBD |

## JWT Operation Benchmarks

### JMH Microbenchmark Results
| Operation | Avg Time (ns) | Std Dev | Ops/sec |
|-----------|---------------|---------|---------|
| JWT Token Generation | TBD | TBD | TBD |
| JWT Token Validation | TBD | TBD | TBD |
| JWT Token Parsing | TBD | TBD | TBD |

## Database Operation Timings

### Key Persistence Operations
| Operation | Avg Time (ms) | 95th Percentile |
|-----------|---------------|-----------------|
| Article Insert | TBD | TBD |
| Article Query by Slug | TBD | TBD |
| User Authentication Query | TBD | TBD |
| Comment Insert | TBD | TBD |
| Tag Query All | TBD | TBD |

## Memory Usage Baseline
- **Heap Usage at Startup**: TBD MB
- **Heap Usage under Load**: TBD MB
- **GC Frequency**: TBD collections/min
- **GC Pause Time**: TBD ms avg

## Performance Testing Scripts
- Gatling script: `scripts/performance/api-baseline.scala`
- k6 script: `scripts/performance/load-test.js`
- JMH benchmark: `src/migrationTest/java/io/spring/migration/JwtPerformanceBenchmark.java`
- Database timing: `scripts/performance/db-operations.sh`

## Regression Thresholds
- **Critical endpoints**: 5% performance degradation threshold
- **Secondary endpoints**: 10% performance degradation threshold
- **JWT operations**: 5% performance degradation threshold
- **Database operations**: 10% performance degradation threshold

## Notes
- All measurements should be taken with warmed-up JVM (after 10+ iterations)
- Load testing should simulate realistic user patterns
- Database operations measured with typical data volumes
- Memory measurements taken during steady-state operation
