import redis
from redis import Redis


class RedisClient:
    def __init__(self):
        self._host = 'redis'
        #self._host = 'localhost'
        self._port = 6379
        self.redis = Redis(host=self._host, port=self._port)

    def add_token(self, token) -> bool:
        if self.exists(token):
            return False
        self.redis.set(token, 'blacklisted')
        return True

    def exists(self, token) -> bool:
        if self.redis.exists(token) == 1:
            return True
        return False
