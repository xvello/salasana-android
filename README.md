[![Firefox port](https://img.shields.io/badge/Ported%20on-Firefox-orange.svg?style=popout-square&logo=mozilla-firefox)](https://github.com/xvello/salasana-webextension)
[![Bootstrap version](https://img.shields.io/badge/Ported%20on-Bootstrap-purple.svg?style=popout-square&logo=bootstrap)](https://github.com/xvello/html-password-generator)

# Salasana Password Generator - Android

This Android app allows you to generate site-specific password from a single master password. This avoids the hassle of remembering a unique password for each website you sign up to.

[<img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png"
     alt="Get it on F-Droid"
     height="80">](https://f-droid.org/packages/net.xvello.salasana/)

## Why should I use it ?
We all have dozens (or even hundreds) of website accounts we have to remember. We are then tempted to reuse the same password for several websites. If one of them is hacked or malevolent, you risk exposing your other accounts.

## How does it work?
It mixes  together your personal master password and the website domain using a little cryptographic magic we call SHA-1. It will always get the same result if given that domain name and master password, but will never get that result if either changes. (Well, once in a few billion times it might.)

## Thanks

Based on [Nic Wolff's password generator](http://angel.net/~nic/passwd.current.html) version 11 apr 2014
