This is a bare-bones companion application for the annual [Sun God Festival](
http://sungodfestival.ucsd.edu). It is intended to serve the same purpose as the
paper festival guides.

## Setting up the repository

I screwed up the submodule reference by committing changes to ActionBarSherlock,
so you will need to do this yourself (sorry!)

    pushd libs
    rm -rf ActionBarSherlock
    git clone http://github.com/JakeWharton/ActionBarSherlock
    cp -f ${ANDROID_SDK_DIR}/extras/android/support/v4/android-support-v4.jar \
            ActionBarSherlock/actionbarsherlock/libs/
    popd

Note that `ANDROID_SDK_DIR` is not defined. You should replace it with the path
to your Android SDK install (e.g. `$HOME/android-sdk-linux`,
`/Users/Shared/android-sdk-macosx`, `%USERPROFILE%\\android-sdk-windows`).

## Lineup

The _Lineup_ section of the application depends on a few files for the lineup
that are not included in the repository, mostly because the lineup should only
be released when it's ready and it probably isn't a good idea to commit old
lineups. So you will need to create your own lineup files for testing.

The schedule for each stage is stored in a separate XML file in `res/xml/`. The
_Lineup_ section loads these files by reading `@array/lineup_files` from
`res/values/arrays.xml` and assigns each stage the corresponding name listed in
`@array/stage_names` (also in `res/values/arrays.xml`).

Each schedule file should follow roughly the following format:

    <?xml version="1.0" encoding="utf-8"?>
    <lineup>
        <!--
        Change the attribute values to correspond to the actual performance
        data. (with) is an optional attribute; if the artist does not have an
        opener, do not include the attribute.

        Set times are expressed in the format HH:MM in 24-hour style. Sets that
        end at midnight SHOULD be truncated at 23:59 to avoid rendering issues
        (known bug).

        The (detail) value should refer to a layout file in res/layout/ BY NAME
        (i.e. if the file is res/layout/artist_detail_phish.xml, then the value
        of the (detail) attribute should be "artist_detail_phish"). This layout
        will be used when the user touches a performance item.
        <set artist="Artist name"
                with="Opener name (optional attribute)"
                begin="15:00"
                end="15:30"
                detail="artist_detail_layout_name"
        />
        <!-- more sets here ... -->
    </lineup>

Since `@array/lineup_files` has already been populated with

    <string-array name="lineup_files">
        <item>main_stage_lineup</item>
        <item>dance_stage_lineup</item>
        <item>midway_lineup</item>
    </string-array>

the files required for basic operation are

* `res/xml/main_stage_lineup.xml`
* `res/xml/dance_stage_lineup.xml`
* `res/xml/midway_lineup.xml`
