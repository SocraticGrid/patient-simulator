rule "[EXECUTOR] Log $disease$"
when
    \$token: ValueModifiedToken()
then
    FileLogger.getInstance().logData("$disease$",\$token);
end