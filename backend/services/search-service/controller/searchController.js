const searchService = require('../service/searchService')
const search = async(request,response) =>{
    const {from,to,date} = request.query;
    try{
        const flights = await searchService.searchFlights(from,to,date);
        response.json(flights);
    }catch(error){
        console.error('error:', error);
        response.status(500).json({error:'Internal Server Error'})
    }
};
module.exports={search};
