package balanceLoanBooks.springified.dependencies.repo;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

@Slf4j
public class AbstractRepo<T> {

    protected String dataset = "large";
    protected CsvMapper mapper;
    protected List<T> data;

    public AbstractRepo() {
        String dataset = System.getProperty("dataset");
        if (StringUtils.isNotBlank(dataset) && "large".equalsIgnoreCase(dataset)) {
            this.dataset = dataset;
        }

        this.mapper = new CsvMapper();
    }

    protected List<T> loadAll(Class<T> clazz, String file) throws IOException {
        String path = dataset + "/" + file;
        MappingIterator<T> iter =
              mapper.readerWithSchemaFor(clazz).with(CsvSchema.emptySchema().withHeader()).readValues(this.getClass().getClassLoader().getResource(path));
        return iter.readAll();
    }


    protected List<T> getAll(Class<T> clazz, String file) {
        if (data == null) {
            try {
                data = loadAll(clazz, file);
            } catch (IOException e) {
                log.error("Unable to load data from file " + file, e);
                throw new IllegalStateException("Unable to load data from file " + file);
            }
        }

        return data;
    }

    protected List<T> getAll(Class<T> clazz, String file, Comparator sortFunction) {
        if (data == null) {
            try {
                data = loadAll(clazz, file);
                data.sort(sortFunction);
            } catch (IOException e) {
                log.error("Unable to load data from file " + file, e);
                throw new IllegalStateException("Unable to load data from file " + file);
            }
        }

        return data;
    }

}
