const redis = require('redis');
require('dotenv').config();

const client = redis.createClient({
  url: `redis://${process.env.REDIS_HOST || 'localhost'}:6379`
});

client.on('error', (error) => console.log('Redis Client Error', error));
(async () => {
  await client.connect();
  console.log('Connected to Redis');
})();
module.exports = client;