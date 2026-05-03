require('dotenv').config();
const axios = require('axios');
const sendSms = async (to, body) => {
  try {
    const formattedNumber = to.replace('+', '');
    const response = await axios.post(
      'https://www.fast2sms.com/dev/bulkV2',
      {
        route: "q",
        message: body,
        language: "english",
        flash: 0,
        numbers: formattedNumber,
      },
      {
        headers: {
          authorization: process.env.FAST2SMS_API_KEY,
          'Content-Type': 'application/json',
        },
      }
    );
    console.log('SMS sent:', response.data);
    return response.data;
  } catch (error) {
    console.error('SMS error:', error.response?.data || error.message);
  }
};
module.exports = {
  sendSms
};