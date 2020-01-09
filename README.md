TODO: Finish this.

# READ ALL THE TODOS (they are all either not started or incomplete tasks)!
# Cloning this project
To clone this project and get all of its submodules:
```bash
git clone --recurse-submodules git@github.com:Podcrash/DomusInvictus.git
```

# Compiling

To compile all of the projects easily:
```bash
./jooq.sh => run all the JOOQ mappers (this was seperated out to make compiling faster)

./collect.sh bungee => get all the bungee plugins (located in jars)
./collect.sh spigot => get all the spigot plugins (located in jars)
```

When making a new spigot/bungee plugin, make a new task within the project's `build.gradle` file and connect it to jaring.
Example: 

```groovy
// based on LobbyPlugin's build.gradle

task bungee(dependsOn: jar) {

}
```