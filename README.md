# <img src="app/src/main/res/mipmap-xxxhdpi/app_icon_foreground.png" align="left" width="100"> Budgetize
Android application for tracking incomes and expenses

### Project description

* Open source Android Application for tracking incomes and expenses.

### Scope

* **To be available for anyone, for free.**
* **Empower people to mantain a positive balance between income and expenses.**

### Demo

Watch the demo to see all the screens and features of the app:

[Demo](https://user-images.githubusercontent.com/41454051/164342308-e1125262-1716-429f-9626-c7705996f79d.mp4)

### Main features
* Import all bank transactions from any Bank Account. *<span style="color: red">NOTE: currently this feature is implemented using a sandbox API from Open Bank Project. It is just used just as a proof of concept. Feel free to contribute to find alternatives to make this feature ready for production.</span>*
* Add cash transactions
* Compute & visualize income vs expense
* Lots of other features that can be seen in the [Demo](https://youtu.be/Z9uNCqCNr18)

* Written in Java

### Development setup

* Compatible with Android Studio Chipmunk 2021.2.1
* Java 8
* Android API 32

Phone/Emulator setup:
* You need to have at least one Biometric authentication enabled, otherwise, you can't pass the first screen.
* Cloud button will not work, unless you setup the backend server. It is meant to allow you save all your data on the server and access it from another device. At the moment, the app sends only some dummy data to the server. The process also involves Google Sign-In.
* For using the feature to import sandbox transactions from bank accounts, you first need to setup the backend server, perform the Google Sign-In(which will be validated also by the backend server) and then the feature will be available. You can skip all the setup by activating bank_accounts_button. Also, you need to configure the API secrets for OpenBankProject. Get them from here: https://apisandbox.openbankproject.com/

### Concerns
* As mentioned previously, the most important feature "Import bank account transactions" is currently using a sandbox API. Here we have two concerns:
  * This kind of Middleware solutions(which aggregates multiple banks into a single API) are not free. This means we couldn't provide Budgetize free because of this.
  * To use the production API of any Bank or Middleware platform, we need to have some certifications/licenses. Checkout this resources:
    * https://digital-strategy.ec.europa.eu/en/policies/eidas-regulation
    * https://www.varonis.com/blog/psd2
    * https://ec.europa.eu/info/law/payment-services-psd-2-directive-eu-2015-2366_en

## Contributions
* If you want to add a new feature or fix anything just fork, code, commit, push and submit a pull request.
* I encourage anyone to come up with any idea, especially on how to overcome the concerns from above. Add discusions!
* Any suggestion is welcomed.


## Project history
If you got until there, you must be really interested into this project :). Join in the project!
Developed this project for my Bachelor's Degree. Everything started when I got interested about finance, investments, mananging my personal money and finding ways to reduce my expenses. The solution? Budgetize. Keep track of expenses, each of them! As one euro/dollar don't seems so much, a lot of transactions of one euro/dollar means a lot! So, my dream was to have an application where I could have almost efortless, an overview of all my expenses. There are four important points I tried to solve:
1. Import transactions easily from multiple bank accounts, from different bank providers.
2. Easily add cash transactions.
3. Display data in an intelligent and helpful manner.
4. All of the above but without adding another recurrent expense :)

I coded this application intensively for 9 months straight and after I presented it I pretty much abandonded my idea. Why? Because of the certifications and licenses needed for this idea to fully work. This would have imply several things: open a company, find a team, investor, obtain certifications/licenses, publish and compete with other providers and most probably ending to charge users money for using it. But after two years, I thought: what if I make this project open source and with the power of other contributors, I could really bring my idea to life? Where going to see...


