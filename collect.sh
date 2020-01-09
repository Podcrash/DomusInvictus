echo "Starting... Running the gradle task to collect spigot files"
echo "$root"
if [ !  -d "jars" ]; then
	`mkdir jars`
else
	`rm -r jars`
	`mkdir jars`
fi

type="$1" 
if [[ ( $type != "spigot" && $type != "bungee" ) ]]; then
	echo "$type must be either spigot or bungee, Stopping!"
	exit 1
fi

echo "Creating all the jars for $type"
target="jars/$type/"
`mkdir $target`

./gradlew $type

for d in */ ; do
	file=./$d"build.gradle"
	if [ -e "$file" ]; then
		builddir="$d""build/libs"
		#echo "$builddir"
		for f in ./$builddir/*.jar ; do
			echo "Moving $f to $target"
			`mv $f $target`
		done
	fi
done
	