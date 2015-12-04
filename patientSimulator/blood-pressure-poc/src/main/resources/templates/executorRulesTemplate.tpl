import com.cognitive.bp.poc.logger.*;
import com.cognitive.bp.poc.model.*;
import com.cognitive.bp.poc.recommendation.patient.*;
import com.cognitive.data.*;

global JCpSimDataFileLogger fileLogger;

rule "[EXECUTOR] Log in File"
when
    \$data: JCpSimData()
    \$clock: SimulationClockToken(associatedTo == \$data)
then
    if (fileLogger != null){
        fileLogger.processJCpSimData(\$data, \$clock);
    }
end
