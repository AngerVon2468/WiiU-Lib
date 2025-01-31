package wiiu.mavity.wiiu_lib.util.process;

@SuppressWarnings("unchecked")
public interface ResourceContainer extends OpenableAutoCloseable<ResourceContainer> {

	void createResources();

	@Override
	default ResourceContainer open() throws Exception {
		return this;
	}

	@Override
	void close() throws Exception;
}