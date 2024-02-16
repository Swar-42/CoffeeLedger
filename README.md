## About

A simple ledger and command-line interface for coworkers to track coffee payments and decide who should pay next, written in Java. \
Uses an embedded [h2](https://www.h2database.com/html/main.html) SQL database to store and retrieve information. \
Written for a [Bertram Labs](https://www.bertramlabs.com/) coding challenge. \
\
Features (Command Line Interface):
- Track people's payments
- Process coffee orders as a group of people
- Provide recommendations on who should next pay for coffee
- Save person information, order information, and group order information for easy processing
- Add/remove people, orders, and group orders
- Edit prices/names/ledger info for people and orders

## Running CoffeeLedger
Prerequisites: Latest version of [Java Runtime Environment (JRE)](https://www.java.com/en/download/manual.jsp)
1. Download the latest [CoffeeLedger.jar release](https://github.com/Swar-42/CoffeeLedger/releases) from this repository.
2. Open a command-line in the .jar's directory.
3. Run the command `java -jar CoffeeLedger.jar`
<!-- -->
And that's it! In the directory will be a file `coffeeledger.db.mv.db` which saves all data in the program. \
If this file is lost, then saved data from the CoffeeLedger program will be lost.

## Assumptions made for the coding challenge

I've worked very hard to make the program quite robust, and so it contains all features important to tracking group coffee payments. \
However, here are the guidelines that this program works within: 
* No two people, orders, group orders can have the exact same name (though the names can be edited!)
* There are no concerns of security (no one is trying to rig the program in their favor)
* Group order history does not need to be tracked (though this could be added as a feature!)
* Previous group orders do not need to be substantially edited (this seems to me to be the next feature to add!)
    * New group orders can always be added, though.
<!-- -->
I hope the fellows at Bertram Labs enjoy my solution to this problem! \
I believe it aptly represents some of my skills as a software developer, and I hope to join the team in the near future.
