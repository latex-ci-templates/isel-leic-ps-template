# Project Documentation

## Table of Contents

- [Documents](#documents)
- [Instructions](#instructions)
  - [Configure LaTeX environment in Intellij IDEA](#configure-latex-environment-in-intellij-idea)
  - [Add a new subproject](#add-a-new-subproject)
  - [Compile and build all subprojects](#compile-and-build-all-subprojects)
    - [Manually](#manually)
    - [Automatically](#automatically)

## Documents

- [project-report](project-report/out/main.pdf)
- [project-proposal](project-proposal/out/main.pdf)
- [project-org](project-org/out/main.pdf)

## Instructions

### Configure LaTeX environment in Intellij IDEA

- Install [MikTeX](https://miktex.org/download);
    - Update `MikTeX` packages: `MikTeX Console -> Updates -> Check for updates`;
- Install [SumatraPDF](https://www.sumatrapdfreader.org/download-free-pdf-viewer) viewer;
- Install [TeXiFy IDEA](https://plugins.jetbrains.com/plugin/9473-texify-idea) plugin;
- Configure inverse-search in `Intellij IDEA` for SumatraPDF: `Tools -> LaTeX -> Configure Inverse Search`, which
  enables
  the user to jump from the pdf to the corresponding tex file source.

### Add a new subproject

1. Create in root folder a new folder with the name of the subproject;
2. Go to [settings.gradle.kts](settings.gradle.kts) and add the new subproject with:
    ```kotlin
    include(":subproject-name")
    ```
3. Compile and build pdf, resolving any bibliographic references, with the Gradle task `buildPdf` using the Gradle Panel
   or the following command in the terminal:
    ```bash
    ./gradlew :subproject-name:buildPdf
    ```

4. **[Optional]** Configure alternative names for the subproject directories and the main tex file in
   the `build.gradle.kts` file:
    - Create a `build.gradle.kts` file in the subproject directory;
    - Add the following code:

       ```kotlin
       ext.set("variable", "value")
       ```
      | Variable          | Default value | Description                                                                     |
      |-------------------|---------------|---------------------------------------------------------------------------------|
      | `srcDirName`      | `src`         | name of the tex source file's directory                                         |
      | `outDirName`      | `out`         | name of the output directory, where the pdf file will be generated to           |
      | `auxDirName`      | `auxil`       | name of the auxiliary directory, where the auxiliary files will be generated to |
      | `mainTexFileName` | `main`        | name of the main tex file                                                       |
      | `useBibtex`       | `true`        | use bibtex to resolve bibliography references                                   |

5. **[Optional]** If no changes are made to the bibliography references in development, it is possible to speed up the
   compilation process by using IDEA's run configuration in the `<main>.tex` file instead of the Gradle task `buildPdf`,
   as the latter has more compile iterations to resolve the bibliography references.
    - In the run configuration panel, edit the paths to the corresponding subproject directories:

   | ![Run Configuration](docs/gifs/idea-main-tex-configuration.gif) |
   |:---------------------------------------------------------------:|
   |                    *Edit Run Configuration*                     |

    - To compile the document use the shortcut `Shift + F10` or the gutter icon `Run`.

> [!IMPORTANT]
> It is advised to not delete the generated auxiliary files as they can be used in subsequent compilations to speed up
> the process.

### Compile and build all subprojects

#### Manually

To compile and build all pdfs in all subprojects, use the Gradle task `buildAllPdfs` which can be found in the Gradle
Panel or by running the following command in the terminal:

```bash
./gradlew buildAllPdfs
```

#### Automatically

A [workflow using github actions](.github/workflows/compile-and-deploy-all-documents.yaml) is set up
to automatically compile
and build all pdfs in all subprojects when a `push`is made to the repository in the `main` branch or from
a `pull request`.
