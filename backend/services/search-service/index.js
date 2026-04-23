const express = require('express');
const cors = require('cors');
require('dotenv').config();
const searchRoutes = require('./routes/searchRoutes');
const app = express();
const port = process.env.PORT || 8085;
app.use(cors());
app.use(express.json());
app.use('/api/v1.0/flight', searchRoutes);

app.get('/health', (request, response) => {
  response.json({ status: 'UP', service: 'search-service' });
});
app.listen(port, () => {
  console.log(`Search service listening at http://localhost:${port}`);
});
