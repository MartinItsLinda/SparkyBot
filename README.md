# API

The Module API and base for functions of the Sparky Discord Bot.

http://www.sparkydiscordbot.uk 

If you notice any typos or want to make adjustments to this feel free to do so.

**Getting Started**

##Creating Your First Module

### First steps

```java
import uk.sparkydiscordbot.api.entities.module.ModuleData;
import uk.sparkydiscordbot.api.entities.module.Module;

@ModuleData(name = "HelloWorld", version = "1.0.0")
public class HelloWorld extends Module {
    
    @Override
    public void onEnable() {
        this.getLogger().info("Hello World!");
    }
    
    @Override
    public void onDisable() {
        this.getLogger().info("Goodbye World!");
    }
    
}
```

**Overview**

We start off the day by creating main class for our Module, after creating it we annotate it with `ModuleData`,
this annotation contains all the information needed of a module and is essential part before it can begin to take its first steps into the wild.

`@ModuleData#name`
This is just the name of the Module, a name just serves as an identifier for it so if something goes wrong or it gets upset, we know who it was
and we can help to get them back on track and working.

`@ModuleData#version`
This is the current version of the Module, it's here so we can tell if it needs updating or if everything's running smoothly
so make sure it's changed every time you update your code as it's essential. We follow Major, Minor, Patch for our version numbers

Major = Major changes to code that will break anything and everything in its path, this will be most likely to need changing when the API itself gets updated, perform thorough testing before considering release to a dev branch
Minor = Small changes, added features, nothings broken and things stay fresh and new.
Patch = Interchangeable update, upgrade/downgrade without any issues at all (bug fixes, performance improvement, code tweaks)

After this we then extend the `Module` class which signifies this as being the main class of a Module and is just as essential as the annotation, after extending 
it gives us the ability of overriding the methods `Module#onEnable` and `Module#onDisable` and is our second step to becoming a functional module.

`Module#onEnable`
 
This method is called after a Module has been enabled and is where all your initialization
of classes, registering commands, events and the like should go.

Commands and events can be registered past this method but it isn't advised (more of a consistency thing rather than risk of breaking something)

`Module#onDisable` 

This method is called before as your Module is being disabled and should be used to close your resources, don't worry about
things you call from the API, these are handled directly and need not have Module interaction.

Commands, events and scheduled tasks are automatically unregistered/terminated after your Module is disabled so there's no need
to do any of these yourself.

##The Command System

### Writing your first command
```java
import uk.sparkydiscordbot.api.entities.command.CommandExecutor;
import uk.sparkydiscordbot.api.entities.command.CommandContext;
import uk.sparkydiscordbot.api.entities.command.Arguments;
import uk.sparkydiscordbot.api.entities.command.parsers.ArgumentParsers;

import net.dv8tion.jda.api.entities.Member;

public class MyCommand implements CommandExecutor {
    
    @Override              
    public boolean execute(final CommandContext context, final Arguments args) {
        final Member member = args.next(ArgumentParsers.MEMBER);
        
        context.reply("Boop! " + member.getAsMention());
        
        return true;
    }
    
}
```

**Overview**:

We create our new class called "MyCommand" implementing `CommandExecutor`, this is something that ALL commands MUST extend in order to be registered.

Implementing the `CommandExecutor#execute` method we get given two parameters

`CommandContext`

This contains information such as the user that executed the command, the channel it was executed in, the server etc, basically everything
that lead up to the command being triggered and some useful things like a translation class so we can respond to users in the language
they want to but we will talk more about that at another point.

`Arguments`

This contains the arguments that were executed with this command and other things like the flags executed with it.

On command execution we then retrieve some information from the arguments the user passed to us by calling `args.next(ArgumentParser)` and in this case there's only one argument, then we respond with a message giving the user a ping.

You might ask what if the user doesn't mention anyone, you will get a null pointer or something will fail.
However in this case it's perfectly fine to call args.next(ArgumentParser) without fear of getting exceptions or having null pointers thrown at you (at least for inbuilt parsers, as you move on you can create your own and use them as you would any other),
should no argument be passed when you're expecting there to be one or they give something entirely unrelated to it the command handler will manage this on its own and give the user a polite nudge saying how they should use the command.

There are parsers that don't mind not getting given a value but they will either have defaults on them or it is not required for the parser to function in how you use it.
Should the parser not have a default or a default that isn't useful to you, you can also specify your own return value for a parser (be it inbuilt or your own) by just calling

```java
uk.sparkydiscordbot.api.entities.command.parsers.ArgumentParsers.alt(ArgumentParser<T> parser, T fallback)
```

First argument being the parser you want to give a different return value of and the second being the fallback argument of course being of the same type as the ArgumentParser returns.

There's a lot of options when it comes to processing arguments for your own command, you can even use multiple parsers of different return values, of course allowing for those
variations in possible responses in your code.

### Making your command usable
```java
import uk.sparkydiscordbot.api.entities.module.CommandRegistry;
import uk.sparkydiscordbot.api.entities.command.Command;
  
...

@Override
public void onEnable() {
    
    final CommandRegistry commandRegistry = this.getCommandRegistry();

    commandRegistry.registerCommand(Command.builder()
             .names("boop", "boops")
             .executor(new MyCommand())
             .build());

}
```

**Overview**:

In our onEnable method we retrieve our CommandRegistry instance which is handily created for us by the API, after that
we can then create a new Command builder by calling `uk.sparkydiscordbot.command.Command#builder`, this
is a useful method allowing you to configure your command for how it's to be used.

We specify our commands name (or trigger) this is what users can type to run our command
and we also give an alias to give an alternate way to execute the command. Running the command with an alias
doesn't change the way a command will be processed and command aliases must be unique, giving your command an alias
already used by another command (within the bot not just your Module) will prevent it from being registered.

We also specify the actual executor for our new command so that the API knows what it's calling,
there are other methods in the builder but these are the only ones we'll talk about now.

You can also get the trigger (in this instance either boop or boops) that caused your command from `CommandContext`
if you for some reason want to track that information.

### An introduction to flags
```java
import uk.sparkydiscordbot.api.entities.command.CommandExecutor;
import uk.sparkydiscordbot.api.entities.command.CommandContext;
import uk.sparkydiscordbot.api.entities.command.Arguments;
import uk.sparkydiscordbot.api.entities.command.parsers.ArgumentParsers;

import net.dv8tion.jda.api.entities.Member;

public class MyCommand implements CommandExecutor {
    
    @Override
    public boolean execute(final CommandContext context, final Arguments args) {
        
        final Member member = args.next(ArgumentParsers.MEMBER);
        
        context.reply("Boop! " + member.getAsMention());

        if(args.hasFlag("doubleboop")) { //Check the command was executed with one of our registered flags
            request.reply("Double Boop! " + member.getAsMention());
        }
        
        return true;
    }
    
}
```

**Overview**:

Going back to our newly created command we can add a new feature.....Flags!
Flags are a neat way to add different ways for your command to both process information
and respond to the user that executed it.

In this instance we're simply changing the way we respond to the user, we check that the
command was given a flag called "doubleboop" and if the user gave it
then we ping the user twice (development at its finest).

Commands can have as many flags as you want (of course within reason but that's up to you, there is no hard set limit)

When executing this command it would look something like

```text
>boops Samuel#0001 -doubleboop 
```

##The event system

###Creating your own event
```java
import uk.sparkydiscordbot.api.entities.event.Event;

public class MyCustomEvent extends Event {

    private final String message;

    public MyCustomEvent(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

}
```

**Overview**:

I know we're rushing into things a bit, we've barely just gotten into commands and their capabilities but keeping
the train going we're moving onto events.

Implemented inside the API is its own event system, giving you a different way
to process information given to you.

We create a new class and in its constructor we take a string called message,
anywhere inside your module you can send this event to the internal event handler
which will ping it to everyone that's listening to it by calling

`getEventRegistry().callEvent(new MyCustomEvent("Hello There"))`

`getEventRegistry` is a method that exists in the parent class `Module` which you extend in your main class similar to `getCommandRegistry` and likewise is specific to your module,
only you can interact with your events and the same goes for other modules, all modules are kept as separate from each other as can be
and should never be relying on each other for anything whatsoever. 

### Listening to events
```java
import uk.sparkydiscordbot.api.entities.event.EventListener;
import uk.sparkydiscordbot.api.entities.event.EventHandler;
import uk.sparkydiscordbot.api.entities.event.EventListener;

public class MyEventListener implements EventListener {

    @EventHandler
    public void onEvent(final MyCustomEvent event) {
        System.out.println(event.getMessage());
    }
    
}
```

**Overview**:

Just like any other class that will listen to events we must implement the EventListener class,
this is just to make sure that we're giving the right thing when we register it and there's nothing to
inherit from the class itself.

In order to listen to an event we must first annotate a method (in this case we call it onEvent but you can call it whatever you want, the name isn't relevant).
Once the event is fired it will be thrown around and will eventually land in our laps and we can proceed to deal with the information we were given 
by said event, in this case it's our event we made earlier and we get given a message which we just print to the console.

There are loads of events within the API all of which giving their own bit of information from users joining the guild
to people talking in voice channels, any bit of information we get there is likely an event for it.

### Firing your new custom event
```java
import uk.sparkydiscordbot.api.entities.module.EventRegistry;

...

@Override
public void onEnable() {
    ...
    final EventRegistry eventRegistry = this.getEventRegistry();

    eventRegistry.registerEvent(new MyEventListener());

    eventRegistry.callEvent(new MyCustomEvent("Hello Event System."));
}
```

**Overview**:

Now that we've created our event and added something to listen to it we now need a way of firing it,
Every module is given its own `EventRegistry` and similar to commands we can use it to register
our interest in an event so that when the time comes we're part of the cool kids club that get told about it.

But we can also use it to call events as well (this is not limited to your own events), in this case we 
go to our event registry and call the `.register(EventListener)` method which takes our newly created `EventListener`
as its argument, there's no need to register the event class (the one that extends `Event`) itself as this is just the class that gets thrown around and the API
doesn't need to know about it.

###Final note
This guide will continue to grow and possibly end up being moved into their own sections
as the API and bot continue to grow themselves and demands change.

There's plenty of things we haven't covered but hopefully this gives some insight into how things
function and the capability given to modules to become whatever they want to be.