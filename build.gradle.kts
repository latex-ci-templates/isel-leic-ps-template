subprojects {
    println("Configuring subproject ${project.name}")
    // optional variables: retrieve the value of a property or use a default value if it is not set
    // in a submodule's build.gradle.kts a value of a variable can be changed by setting it in the extra map
    // (e.g., ext.set("variable", "value"))
    val auxDirName: String by extra { "auxil" }
    val outDirName: String by extra { "out" }
    val srcDirName: String by extra { "src" }
    val mainTexFileName: String by extra { "main" }
    val useBibtex: Boolean by extra { true }

    // the directory where the submodule is located from the root project
    val moduleDir: String = project.projectDir.toString()

    afterEvaluate {

        if (useBibtex) {
            tasks.register("bibtex") {
                outputs.upToDateWhen { false }
                description =
                    "Compiles the bibliography after the first iteration of pdflatex"
                dependsOn("pdflatex-A")
                exec {
                    setWorkingDir("$moduleDir/$srcDirName")
                    commandLine(
                        "bibtex",
                        "$moduleDir/$auxDirName/$mainTexFileName",
                    )
                }
            }

            tasks.register("build-mid-step") {
                outputs.upToDateWhen { false }
                dependsOn("bibtex")
                finalizedBy("pdflatex-B")
            }
        } else {
            tasks.register("build-mid-step") {
                outputs.upToDateWhen { false }
                dependsOn("pdflatex-A")
                finalizedBy("pdflatex-B")
            }
        }

        tasks.register("buildPdf") {
            outputs.upToDateWhen { false }
            description = "Build the document, including the bibliography and resolving references"
            dependsOn("build-mid-step")
            finalizedBy("pdflatex-C")
        }

        fun configurePdfLatexTask(taskName: String) {
            tasks.register(taskName) {
                outputs.upToDateWhen { false }
                description = "Compiles the latex document without reusing cached files"
                exec {
                    setWorkingDir("$moduleDir/$srcDirName")
                    commandLine(
                        "pdflatex",
                        "-file-line-error",
                        "-interaction=nonstopmode",
                        "-synctex=1",
                        "-output-format=pdf",
                        "-output-directory=$moduleDir/$outDirName",
                        "-aux-directory=$moduleDir/$auxDirName",
                        mainTexFileName,
                    )
                    // needed to ignore the exit value of pdflatex, since it returns 1 for warnings also
                    isIgnoreExitValue = true
                }
            }
        }

        // pdflatex writes information about the bibliography style and .bib file, as well as
        // all occurrences of \cite{...}, to the file main.aux, assuming the main tex file is main.tex
        configurePdfLatexTask("pdflatex-A")

        // When pdflatex is run again, it now sees that a main.bbl file is available! So it inserts the contents
        // of main.bbl.
        // After this step, the reference list appears in the output PDF formatted according to the chosen \bibliographystyle{...},
        // but the in-text citations are still [?].
        configurePdfLatexTask("pdflatex-B")

        // the \cite{...} commands are replaced with the corresponding numerical labels in the output PDF
        configurePdfLatexTask("pdflatex-C")

        // more information at: https://www.overleaf.com/learn/latex/Bibliography_management_with_bibtex#Enter_\(\mathrm{Bib\TeX}\)
    }
}

// Define a custom task that depends on all buildPdf tasks
tasks.register("buildAllPdfs") {
    dependsOn(subprojects.map { project(":${it.path}").tasks.named("buildPdf") })
    description = "Run task to build all pdfs in all subprojects of this project"
}
