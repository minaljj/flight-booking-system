const pool =require('../config/db');
const redisClient = require('../config/redis')

const searchFlights = async(from,to,date) =>{
    const cacheKey = `search:${from||'any'}:${to||'any'}:${to||'any'}`;
  
    try{
        const cachedData = await redisClient.get(cacheKey);
        if(cachedData){
            console.log('Data from redis cache memory');
            return JSON.parse(cachedData);
        }
    }catch(error){
        console.error('error fetching data error:',error);
    }
    
    let query = 'SELECT * FROM flights WHERE is_blocked = false';
    const params = [];
    if(from){
        query = query + 'and from = ?';
        params.push(from);
    } 
    if(to){
        query = query + 'and tp = ?';
        params.push(to);
    }  
    if(date){
        query = query + 'and date(start_date_time)= ?';
        params.push(date);
    }      
    const [rows] = await pool.execute(query,params); 
    try{
        await redisClient.setEx(cacheKey,300,JSON.stringify(rows))
    }catch(error){
        console.error('redis error:',error);
    }
    return rows;
};
module.exports={searchFlights};