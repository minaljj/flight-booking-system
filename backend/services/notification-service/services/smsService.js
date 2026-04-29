const twilio = require('twilio');
require('dotenv').config();

const accountSid = process.env.TWILIO_ACCOUNT_SID;
const authToken = process.env.TWILIO_AUTH_TOKEN;
const client = accountSid && authToken ? twilio(accountSid, authToken) : null;

const sendSms = async (to, body) => {
  if (!client) {
    console.log('Twilio client not configured. Skipping SMS.');
    return;
  }

  try {
    const message = await client.messages.create({
      body,
      from: process.env.TWILIO_PHONE_NUMBER,
      to
    });
    console.log('SMS sent: %s', message.sid);
    return message;
  } catch (error) {
    console.error('SMS error:', error);
  }
};

module.exports = {
  sendSms
};