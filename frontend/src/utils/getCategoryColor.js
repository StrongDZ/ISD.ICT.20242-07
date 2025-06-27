export const getCategoryColor = (category) => {
    switch (category?.toLowerCase()) {
        case "book":
            return "primary";
        case "cd":
            return "warning";
        case "dvd":
            return "secondary";
        case "lp":
            return "success";
        default:
            return "default";
    }
};