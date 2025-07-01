# Fast Word Study
When loading this mobile app for first time, please give it 5 minutes to prepare database of words. And a bit longer if free space is lesser on mobile device. I brought it down from 30 minutes.  
Word Quiz mobile app: It contains 50 alphagrams per page which can be solved in random order (Words which you already know at first).  
It covers all words by different lengths in descending order of probability across several pages.  
If a certain page is incompletely solved, then you can go to the next page and solve some of the known alphagrams there and come back to the incompletely solved page with all of your progress saved.  
Words can be either submitted with tags or submitted without tags.  
Click on any of alphagrams to see definitions and long click on any of alphagrams to see all unsolved answers.  
Report page is used for tracking timings and for filtering words by tags.
___
Custom Quiz can be used to filter words, for example:
1. `word like '____ING' and back not like '%S%'` for all 7 letter words ending in ING and without back hook S.
2. `length = 5 and word like '%X'` for all 5 letter words ending in X.
3. `length = 7 and tag = 'Unknown'` for all 7 letter words which you tagged as Unknown.
4. `length = 4 order by word` for listing all 4 letter words in alphabetical order.
___
Custom Query is used for making direct changes to the database, for example:
1. `insert into tags values('Chemical', '#A6CAF0')` for adding a Chemical tag with RGB colour code of (166, 202, 240).
2. `update words set solved = 0 where length = 7 and tag = 'Unknown'` for removing solving of all 7 letter words which you tagged as Unknown.
___
Non-tech savvy people may look at [W3Schools website](https://www.w3schools.com/sql/default.asp) for SQL tutorial.  
Some of important SQL queries can also be added as menu options if it were to be distributed for public use, but it was not needed for my personal use.  
Some of other features can also be added if it were to be distributed for public use, such as zooming in and zooming out through all the grid words and pushing lesser important buttons to the menu bar and have more space at the bottom of the page to display definitions of words, common suffixes, validity in older versions of CSW, NWL, CEL and WIMS dictionary and listing all words with 1 letter change from current word.
