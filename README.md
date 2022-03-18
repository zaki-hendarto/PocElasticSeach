# Poc simple search using ElasticSeach

_**INSTALL ELASTIC SEARCH**_
- Download **elasticsearch-8.1.0-windows-x86_64.zip**
- Extract **elasticsearch-8.1.0-windows-x86_64.zip**
- Disable security and listen all address, replace **config\elasticsearch.yml** 
- Start elastic search **bin\elasticsearch.bat**

_**CREATE INDEX BOOK**_
- List all Index : run postman-collection item **ListIndices**
- Create Index Book : run postman-collection item **Create Index**
- Detail Structure Index Book : run postman-collection item **Detail Index**
- Push data Index Book : run postman-collection item **Push Data**
- Delete Index Book : run postman-collection item **Delete Index**

_**SEARCH INDEX BOOK**_
- Search fuziness without paging and sorting : run postman-collection item **Seach fuzzi**
- Search fuziness with paging and sorting : run postman-collection item **Seach fuzzi paging sorting**
- Search auto complete multi match : run postman-collection item **AutoComplete Multi** 
- Search auto complete prefix match : run postman-collection item **AutoComplete Match Prefix**
- Search suggest word : : run postman-collection item **Suggest word** 

_**SEARCH INDEX BOOK SPRINGBOOT APP**_
- Search fuziness with paging : **http://hostname:port/search/fuzzi/{page}/{field-name}/{keyword}**  
  example: http://localhost:8080/search/fuzzi/0/title/belajar  
- Search auto complete : **http://hostname:port/search/auto-complete/{keyword}**  
 example: http://localhost:8080/search/auto-complete/belajar%20g  
- Search suggestion : **http://hostname:port/search/suggest/{keyword}**  
  example: http://localhost:8080/search/suggest/belanja%20piton%20jawa  
