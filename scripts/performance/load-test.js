import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

export let errorRate = new Rate('errors');

export let options = {
  stages: [
    { duration: '30s', target: 10 },
    { duration: '1m', target: 50 },
    { duration: '2m', target: 50 },
    { duration: '30s', target: 0 },
  ],
  thresholds: {
    http_req_duration: ['p(95)<2000'],
    http_req_failed: ['rate<0.05'],
    errors: ['rate<0.05'],
  },
};

const BASE_URL = 'http://localhost:8080';

export default function () {
  let responses = {};

  responses.tags = http.get(`${BASE_URL}/tags`);
  check(responses.tags, {
    'tags status is 200': (r) => r.status === 200,
    'tags response time < 500ms': (r) => r.timings.duration < 500,
  }) || errorRate.add(1);

  sleep(0.1);

  responses.articles = http.get(`${BASE_URL}/articles?limit=10&offset=0`);
  check(responses.articles, {
    'articles status is 200': (r) => r.status === 200,
    'articles response time < 1000ms': (r) => r.timings.duration < 1000,
    'articles has data': (r) => JSON.parse(r.body).articles !== undefined,
  }) || errorRate.add(1);

  sleep(0.1);

  const userId = Math.floor(Math.random() * 10000);
  const registerPayload = JSON.stringify({
    user: {
      username: `testuser${userId}`,
      email: `test${userId}@example.com`,
      password: 'password123'
    }
  });

  responses.register = http.post(`${BASE_URL}/users`, registerPayload, {
    headers: { 'Content-Type': 'application/json' },
  });
  
  let authToken = null;
  const registerSuccess = check(responses.register, {
    'register status is 200': (r) => r.status === 200,
    'register response time < 1500ms': (r) => r.timings.duration < 1500,
  });
  
  if (registerSuccess && responses.register.status === 200) {
    const registerData = JSON.parse(responses.register.body);
    authToken = registerData.user.token;
  } else {
    errorRate.add(1);
  }

  sleep(0.1);

  const loginPayload = JSON.stringify({
    user: {
      email: `test${userId}@example.com`,
      password: 'password123'
    }
  });

  responses.login = http.post(`${BASE_URL}/users/login`, loginPayload, {
    headers: { 'Content-Type': 'application/json' },
  });
  check(responses.login, {
    'login status is 200 or 422': (r) => r.status === 200 || r.status === 422,
    'login response time < 1000ms': (r) => r.timings.duration < 1000,
  }) || errorRate.add(1);

  sleep(0.1);

  responses.profile = http.get(`${BASE_URL}/profiles/testuser`);
  check(responses.profile, {
    'profile status is 200 or 404': (r) => r.status === 200 || r.status === 404,
    'profile response time < 500ms': (r) => r.timings.duration < 500,
  }) || errorRate.add(1);

  if (authToken) {
    sleep(0.1);
    
    responses.feed = http.get(`${BASE_URL}/articles/feed`, {
      headers: { 'Authorization': `Token ${authToken}` },
    });
    check(responses.feed, {
      'feed status is 200': (r) => r.status === 200,
      'feed response time < 1000ms': (r) => r.timings.duration < 1000,
    }) || errorRate.add(1);
  }

  sleep(0.2);
}
