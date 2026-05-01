const express = require('express');
require('dotenv').config();

const notificationRoutes = require('./routes/notificationRoutes');
const app = express();
const port = process.env.PORT || 8086;

app.use(express.json());
app.use('/api/v1.0/notification', notificationRoutes);
app.get('/health', (req, res) => {
  res.json({ status: 'UP', service: 'notification-service' });
});
app.listen(port, () => {
  console.log(`Notification service listening at http://localhost:${port}`);
});
module.exports = app;