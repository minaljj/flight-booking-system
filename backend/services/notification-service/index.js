const express = require('express');
const { loadConfig } = require('./config/configClient');

const startService = async () => {
  await loadConfig();
  const kafkaCosumer = require('./services/kafkaConsumer.js');


  const app = express();
  const port = process.env.PORT;

  app.use(express.json());

  app.get('/health', (req, res) => {
    res.json({ status: 'UP', service: 'notification-service' });
  });


  app.listen(port, () => {
    console.log(`Notification service listening at http://localhost:${port}`);
    //starting kafka consumer
    kafkaCosumer.run().catch(err => console.error('kafka Consumer error:', err));
    
  });
  return app;
};
  startService().catch(err => {
    console.error('Failed to start Notification Service:', err);
    process.exit(1);
  });
