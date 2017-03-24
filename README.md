# Jamphony
Because `symfony` in `ja`va.

**For documentation and to see examples of usage**, check out [this sample project](https://github.com/dsikkema/jamphony-sample-project)

## What's this

A framework for projects composed of console commands. It lets you easily define your input and command logic, and then handles 
the input processing and other console application plumbing for you. 

## Disclaimer

Disclaimer: I don't know how licenses work, so just don't use this in any kind of important way, or in any production software, until
it's ready. Right now it's just a java exercise, and not ready for production.

## Use

0. Create class for command
0. Add command to CommandModule
0. Add CommandModule binding to Guice Module
0. Create entry point that builds the CommandRunner using guice and runs it
