const axios = require('axios');
require('dotenv').config();

/**
 * Resolves Spring Cloud Config placeholders like ${KAFKA_BROKER:localhost:9092}
 * @param {string} value The value to resolve
 * @returns {string} The resolved value
 */
const resolveValue = (value) => {
  if (typeof value !== 'string') return value;
  const regex = /\${([^:}]+)(?::([^}]*))?}/g;
  return value.replace(regex, (match, key, defaultValue) => {
    if (process.env[key] !== undefined) {
      return process.env[key];
    }

    return defaultValue !== undefined ? defaultValue : match;
  });
};
const loadConfig = async () =>{
    const configServerUrl = process.env.CONFIG_SERVER_URL || 'http://localhost:8888';
    const applicationName = 'notification-service';
    const profile = process.env.NODE_ENV || 'default';

try{
    console.log(`Fetching configuration from ${configServerUrl}/${applicationName}/${profile}`);
    const response = await axios.get(`${configServerUrl}/${applicationName}/${profile}`);
    if(response.data && response.data.propertySources){
        for (let i = response.data.propertySources.length - 1; i >= 0; i--) {
        const source = response.data.propertySources[i].source;
        for (const [key, value] of Object.entries(source)) {
          const envKey = key.toUpperCase().replace(/\./g, '_').replace(/-/g, '_');
          process.env[envKey] = resolveValue(value);
        }
      }
      console.log('Configuration loaded successfully from Config Server');
    }
}
catch (error) {
    console.error('Failed to load configuration from Config Server:', error.message);
    console.log('Falling back to local environment variables');
  }
};

module.exports = {loadConfig};