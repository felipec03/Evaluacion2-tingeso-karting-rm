# Kubernetes Concepts Cheat Sheet

## Core Concepts

*   **`apiVersion`**: Specifies the Kubernetes API version for the object definition.
    *   Example: `apps/v1` for `Deployment`, `v1` for `Service`, `ConfigMap`, `Secret`, `PersistentVolume`, `PersistentVolumeClaim`.
*   **`kind`**: Specifies the type of Kubernetes object being defined.
    *   Examples: `Deployment`, `Service`, `ConfigMap`, `Secret`, `PersistentVolume`, `PersistentVolumeClaim`, `Pod`.
*   **`metadata`**: Data that helps uniquely identify a Kubernetes object.
    *   `name`: The unique name of the object.
    *   `labels`: Key/value pairs attached to objects for organization and selection.
*   **`spec`**: The desired state of a Kubernetes object, defined by the user. It describes what the object should look like and how it should behave.
*   **`Labels`**: Key/value pairs attached to Kubernetes objects (like Pods and Services). They are used to organize and select subsets of objects.
    *   Example: `app: frontend`
*   **`Selector`**: Used by Services to determine which Pods to route traffic to, and by Deployments (and other controllers) to manage their Pods. Selectors match objects based on their labels.

## Workload Resources

*   **`Pod`**: The smallest deployable unit in Kubernetes.
    *   Represents a single instance of a running process.
    *   Can contain one or more containers that share storage and network resources.
    *   Typically managed by higher-level controllers like Deployments.
*   **`Deployment`**: A Kubernetes controller that manages stateless applications.
    *   Ensures a specified number of replicas of your application's Pods are running.
    *   Handles updates and rollbacks.
    *   Key fields:
        *   `replicas`: Desired number of Pod instances.
        *   `selector` (using `matchLabels`): How the Deployment identifies the Pods it manages.
        *   `template`: Blueprint for creating Pods (defines containers, volumes, etc.).
        *   `strategy` (`type`): How updates are rolled out (e.g., `Recreate`, `RollingUpdate`).

## Service Discovery and Load Balancing

*   **`Service`**: An abstract way to expose an application running on a set of Pods as a network service. Provides a stable IP address and DNS name.
    *   **`type: ClusterIP`**:
        *   Exposes the Service on a cluster-internal IP.
        *   Only reachable from within the cluster.
        *   Default type.
    *   **`type: LoadBalancer`**:
        *   Exposes the Service externally using a cloud provider's load balancer (or `minikube tunnel` locally).
        *   Used for services needing external access.
    *   **`ports`**: Defines port mapping.
        *   `port`: Port on which the Service is exposed.
        *   `targetPort`: Port on the Pods/containers to which traffic is forwarded.

## Configuration

*   **`ConfigMap`**: Stores non-confidential configuration data in key-value pairs.
    *   Pods can consume `ConfigMap`s as environment variables, command-line arguments, or configuration files in a volume.
*   **`Secret`**: Stores small amounts of sensitive data (passwords, tokens, keys).
    *   Can be mounted as data volumes or exposed as environment variables.

## Storage

*   **`Volume`**: A directory, possibly with data, accessible to Containers in a Pod.
    *   **`persistentVolumeClaim`**: Used to request persistent storage.
    *   **`configMap`**: Makes `ConfigMap` data available as files within a Pod.
*   **`PersistentVolume (PV)`**: A piece of storage in the cluster, provisioned by an administrator or dynamically. Independent of any individual Pod.
*   **`PersistentVolumeClaim (PVC)`**: A request for storage by a user. Consumes PV resources. Pods request specific sizes and access modes.

## Container Specification (within a Pod template)

*   **`Container`**: A runnable instance of a software image.
    *   `image`: The Docker image to be run (e.g., `felipec03/frontend:latest`).
    *   `ports` (`containerPort`): The port on which the application inside the container is listening.
    *   `env`: Environment variables passed to the container.
        *   Can be defined directly.
        *   Sourced from `ConfigMap`s (`configMapKeyRef`).
        *   Sourced from `Secret`s (`secretKeyRef`).
    *   `volumeMounts`: Specifies where a volume should be mounted inside a container.
        *   `name`: Name of the volume to mount.
        *   `mountPath`: Path within the container where the volume is mounted.
        *   `readOnly`: (Optional) If true, mount is read-only.
    *   `imagePullPolicy`: Defines when Kubernetes should pull the container image (e.g., `Always`, `IfNotPresent`, `Never`).