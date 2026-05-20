package solis3d.projectvaultbackend.payloads;

public record AdminStatsDTO(
        long usersCount,
        long projectsCount,
        long publicProjectsCount,
        long privateProjectCount
) {
}
