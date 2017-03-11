Crawler

Programming language : Java
Library: Crawler4j, jSoup, Gson, Json

I choose to get information about reviews from rating portal Heureka.cz. The extracted data are save in JSON and contain structurated information about reviews. Every review contains text, pluses, minuses, usefullness of review, date, shop where the item was bought and rating of that product.

Robot.txt (see. https://www.heureka.cz/robots.txt) contains:
"
User-agent: *
Disallow: /direct/mapa/*
Disallow: /direct/ajax/*
Disallow: /exit/*
Disallow: /direct/js/*
Disallow: /f:*:*,*

Sitemap: https://www.heureka.cz/sitemap_index.xml
Sitemap: http://www.heureka.cz/sitemap-http_index.xml
"
The crawler is set to respect this setting (through hard-coded regural expression) and because there is no requirement about crawler delay, it was set to 1000ms to not overload the websites. Sitemap.xml wasn't exploit, because we start crawling at main page and it can lead us to all pages that we want and we dont need another seeds.

For scraping information from pages was used jSoup which used XPath to get elements with required information.

User-agent string was choose according this websites (https://www.keycdn.com/blog/web-crawlers/) like "	Mozilla/5.0 (compatible; lorenpe2bot/1.0)". It is good practise to have word "bot" in User-Agent to let websites know that there is human browsing their pages and can disconnect this bot if necessary.

