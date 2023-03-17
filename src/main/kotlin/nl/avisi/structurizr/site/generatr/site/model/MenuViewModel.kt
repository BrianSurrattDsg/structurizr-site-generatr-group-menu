package nl.avisi.structurizr.site.generatr.site.model

import nl.avisi.structurizr.site.generatr.includedSoftwareSystems
import nl.avisi.structurizr.site.generatr.site.GeneratorContext

class MenuViewModel(generatorContext: GeneratorContext, private val pageViewModel: PageViewModel) {

    val generalItems = sequence {
        yield(createMenuItem("Home", HomePageViewModel.url()))

        if (generatorContext.workspace.documentation.decisions.isNotEmpty())
            yield(createMenuItem("Decisions", WorkspaceDecisionsPageViewModel.url(), false))

        if (generatorContext.workspace.model.softwareSystems.isNotEmpty())
            yield(createMenuItem("Software Systems", SoftwareSystemsPageViewModel.url()))

        generatorContext.workspace.documentation.sections
            .sortedBy { it.order }
            .drop(1)
            .forEach { yield(createMenuItem(it.contentTitle(), WorkspaceDocumentationSectionPageViewModel.url(it))) }
    }.toList()

    val softwareSystemItems: List<LinkViewModel> = generatorContext.workspace.model.includedSoftwareSystems
        .sortedBy { it.name.lowercase() }
        .map { softwareSystem ->
            createMenuItem(
                softwareSystem.name,
                SoftwareSystemPageViewModel.url(softwareSystem, SoftwareSystemPageViewModel.Tab.HOME),
                false
            )
        }

    val groupItems = generatorContext.workspace.model.includedSoftwareSystems
        .filter { softwareSystem -> !softwareSystem.group.isNullOrEmpty() } // gets rid of any software system w/o a group
        .map { softwareSystem -> softwareSystem.group } // converts list of groups to list of
        .flatMap { groupPath ->
            groupPath.split(";") }
        .distinct()
        .map { groupName ->
            createMenuItem(groupName,"http://$groupName",false            )
        }

    private fun createMenuItem(title: String, href: String, exact: Boolean = true) =
        LinkViewModel(pageViewModel, title, href, exact)
}
